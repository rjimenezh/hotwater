/*
 * HotWater Firmware v1.0
 * (c) 2009 Ramón Jiménez
 */
 
//----------------------------------------
// Debug flag - comment out for production
//----------------------------------------


// #define  DEBUG  1
 
//----------------------------------------
// Pin definitions
//----------------------------------------
const int manualOverride  = 3;
const int manualTimer1    = 10;
const int manualTimer2    = 5;
const int manualTimer3    = 6;
const int automatic       = 7;
const int heater          = 8;
const int acPower         = 9;

//----------------------------------------
// State definitions
//----------------------------------------
#define  OFF     0
#define  AUTO    1
#define  MT1     2
#define  MT2     3
#define  MT3     4

//----------------------------------------
// Time unit definitions
//----------------------------------------
#define  HOURS_PER_WEEK     168
#ifdef DEBUG
#define  SECONDS_PER_MINUTE  1
#define  MINUTES_PER_SEGMENT 1
#else
#define  SECONDS_PER_MINUTE  60
#define  MINUTES_PER_SEGMENT 10
#endif
#define  SEGMENTS_PER_HOUR  6
#define  ONE_SECOND  880

//----------------------------------------
// Have we got AC power?
//----------------------------------------
boolean powerIsOn = false;

//----------------------------------------
// Did a scheduled run fail
// because of no AC power?
//----------------------------------------
volatile boolean lastRunFailed = false;
volatile boolean notifyRunFailed = false;

//----------------------------------------
// Failure blink time duty cycle.
// For each second that an automated run
// has failed, how much of it should the
// Auto LED blink?  The remainder of the
// second (the time the LED is off) is
// computed automatically.
//----------------------------------------
#define  FAILURE_BLINK_TIME  200
#define  FAILURE_OFF_TIME  1000 - FAILURE_BLINK_TIME

//----------------------------------------
// Heater state
//----------------------------------------
volatile int state = OFF;

//----------------------------------------
// Manual run minutes
//----------------------------------------
volatile int manualRunMinutes = 0;

//----------------------------------------
// Manual override segments -
// for how many segments should
// automatic scheduled runs be
// inhibited because of a manual
// override run or cancellation?
//----------------------------------------
volatile int manualOverrideSegments = 0;

//----------------------------------------
// Schedule
//----------------------------------------
int schedule[HOURS_PER_WEEK];

//----------------------------------------
// Time control vars.
// 
// THIS IS NOT A CLOCK!
// The following variable names actually mean:
//
// * Seconds within minute (0-59)
// * Minutes within segment (0-9)
// * Segment within hour (0-5)
// * Hour within week (0-167)
//----------------------------------------
int seconds = 0;
int minutes = 0;
int segment = 0;
int hour = 0;

//----------------------------------------
// PC transfer constants
//----------------------------------------
const byte probeCmd[] = { 72, 87, 80 };  // 'HWP' - HotWater Probe
const byte probeResp[] = { 72, 87, 80, 1 };
const byte transferCmd[] = { 72, 87, 84 }; // 'HWT' - HotWater Transfer
const byte transferResp[] = { 72, 87, 84 , 1};
#define TRANSFER_BYTES  HOURS_PER_WEEK + 5

//----------------------------------------
// Button debouncing
//----------------------------------------
#define DEBOUNCE_TIME 250 // milliseconds between consecutive presses

long lastPressed = 0;

//----------------------------------------
// Minimum amount of 'cycles' during which
// the MO button must be pressed in order
// to consider a valid click.  'Cycles'
// merely refers to a for loop - see the
// moPressed() method for details.
//----------------------------------------
#define  NOISE_CLICK  20000

//----------------------------------------
// Software-based time callibration control.
//----------------------------------------
int runHours = 0;

//----------------------------------------
// Schedule set control var.
//----------------------------------------
boolean scheduleSet = 0;

/**
 *----------------------------------------
 * Firmware initialization.
 *----------------------------------------
 */
void setup() {
  pinMode(manualOverride, INPUT);
  digitalWrite(manualOverride, HIGH); // Turn on internal pull-up
  attachInterrupt(1, moPressed, FALLING);
  //
  pinMode(manualTimer1, OUTPUT);
  pinMode(manualTimer2, OUTPUT);
  pinMode(manualTimer3, OUTPUT);
  pinMode(automatic, OUTPUT);
  pinMode(heater, OUTPUT);
  pinMode(acPower, INPUT);
  //
  Serial.begin(57600);
  for(int z = 0; z < HOURS_PER_WEEK; z++)
    schedule[z] = 0;
#ifdef DEBUG
  schedule[1] = B00111111;  // Sunday, 1:00 AM thru 2:00 AM
  schedule[3] = B00110000;  // Sunday, 3:40 AM thru 4:00 AM
#endif
  //
  pinMode(13, OUTPUT);
}


/**
 *----------------------------------------
 * Main loop.
 *----------------------------------------
 */
void loop() {
  if(Serial.available()) {
    handleTransfer();
  }
  else {
    updateTime();
    updateControlPanel();
    waitASecond();
  }
}

/**
 *----------------------------------------
 * Handle a PC data transfer.
 * The Java PC software may
 * in fact attempt two different
 * operations: a probe, in order
 * to detect the serial port that
 * the HotWater device is using;
 * or a transfer proper, where several
 * values are transferred from the PC
 * to the device.
 *----------------------------------------
 */
void handleTransfer() {
    boolean isProbing = true;
    boolean isTransfer = true;
    
    if(Serial.available() < 3)
      return;
    
    for(int i = 0; i < 3; i++) {
      int read = Serial.read();
      if((isProbing) && (read != probeCmd[i]))
        isProbing = false;
      if((isTransfer) && (read != transferCmd[i]))
        isTransfer = false;
    }
    //
    if(isProbing)
      Serial.write(probeResp, 4);
    if(isTransfer) {
      Serial.write(transferResp, 4);
      int bytesRead = 0;
      byte transferData[TRANSFER_BYTES];
      while(bytesRead < TRANSFER_BYTES) {
        while(Serial.available() == 0)
          ;
        transferData[bytesRead++] = Serial.read();
      }
      Serial.write(transferResp, 4);
      Serial.write(bytesRead);
      //
      seconds = transferData[0];
      minutes = transferData[1];
      segment = transferData[2];
      hour = transferData[3] + transferData[4];      
      for(int i = 5; i < TRANSFER_BYTES; i++) {
        schedule[i - 5] = transferData[i];
        if(transferData[i] && !scheduleSet)
          scheduleSet = true;
      }
    }
    
    // Reset run hour counter
    runHours = 0;
}

/**
 *----------------------------------------
 * Time counting function.
 *----------------------------------------
 */
void waitASecond() {
  if(notifyRunFailed) {
    digitalWrite(automatic, HIGH);
    delay(FAILURE_BLINK_TIME);
    digitalWrite(automatic, LOW);
    delay(FAILURE_OFF_TIME);
  }
  else {
    delay(ONE_SECOND);  // Actually 880 ms
    if(seconds % 5 == 0) {
      digitalWrite(13, HIGH);
      delay(10);      // ...890 ms...
      digitalWrite(13, LOW);
      if(scheduleSet) {
        delay(100);  // ... 990 ms...
        digitalWrite(13, HIGH);
        delay(10);  // One second
        digitalWrite(13, LOW);
      }
      else
        delay(110);  // One second
    }
    else
      delay(120);  // Complete one second
  }
}

/**
 *----------------------------------------
 * Updates time variables.
 *----------------------------------------
 */
void updateTime() {
  // Update time
  seconds++;
  updateStateOnSecondChange();
  if(seconds % SECONDS_PER_MINUTE == 0) {
    seconds = 0;
    minutes++;
    if(minutes % MINUTES_PER_SEGMENT == 0) {
      minutes = 0;
      segment++;
#ifdef DEBUG
  printTime(hour, segment);
#endif
      if(segment % SEGMENTS_PER_HOUR == 0) {
        segment = 0;
        hour++;
        callibrateTime();
        if(hour == HOURS_PER_WEEK)
          hour = 0; // The week has changed
      } // Hour change
      
      updateStateOnSegmentChange();
    } // Segment change
    
    updateStateOnMinuteChange();
  } // Minute change
}
  
/**
 *----------------------------------------
 * Performs a software callibration
 * based on extended-run observations,
 * as the Arduino Pro Mini is clocked
 * by a resonator and thus rather imprecise.
 * This particular callibration routine
 * compensates for a 6m 9s drift every
 * 24-hour period (the Arduino runs late).
 * Increments are done at 4-hour intervals
 * to prevent any particular programmed run
 * from being off by more than one minute.
 *---------------------------------------- 
 */
void callibrateTime() {
  runHours++;
  
  // Adds one minute, one second six times a day
  if(runHours % 4 == 0) {
    minutes++;
    seconds += 1;
  }

  // Adds one additional second three times a day
  if(runHours % 8 == 0)
    seconds++;
}


/**
 *----------------------------------------
 * Performs any state changes as required
 * by the fact that a second has elapsed.
 *----------------------------------------
 */
void updateStateOnSecondChange() {
  powerIsOn = (digitalRead(acPower) == HIGH);
  if(!powerIsOn) {
    state = OFF;
    manualRunMinutes = 0;
    manualOverrideSegments = 0;
  }
}

/**
 *----------------------------------------
 * Performs any state changes as required
 * by the fact that a minute has elapsed.
 *----------------------------------------
 */
void updateStateOnMinuteChange() {
  // Update state as required:
  //
  // * Turn off if on-auto, on-auto if off as applicable
  // * If manually timed, check whether elapsed time is up
  //   to turn off, otherwise leave as is (MTn state)
  if((state == OFF) || (state == AUTO)) {
    int currentSchedule = schedule[hour];
    if(currentSchedule & (1 << segment)) {
      if(!powerIsOn) {
        if(!lastRunFailed)
          notifyRunFailed = true;
        lastRunFailed = true;
      }
      else if(manualOverrideSegments == 0) {
        lastRunFailed = false;
        state = AUTO;
      }
    }
    else
      state = OFF;
  } else {
    if(manualRunMinutes > 0)
      manualRunMinutes--;
    if(manualRunMinutes == 0)
      state = OFF;
  }
}

/**
 *----------------------------------------
 * Performs any state changes as required
 * by the fact that a minute has elapsed.
 * This is only used to enforce scheduled
 * runs override by manual runs - the
 * updateStateOnMinuteChange() method handles
 * segment transitions as well.
 *----------------------------------------
 */
void updateStateOnSegmentChange() {
  if(manualOverrideSegments > 0)
    manualOverrideSegments--;
}

/**
 *----------------------------------------
 * Manual override pressed
 * interrupt handler.
 *----------------------------------------
 */
void moPressed() {
  
  // Discard spurious (EMI, false contact
  // or mechanical failure) button 'presses'
  unsigned int count = 0;
  while((PIND & B00001000) == 0)
    count++;
    
#ifdef DEBUG
  Serial.println("******");
  Serial.print(count);
#endif
    
  if(count < NOISE_CLICK) {
#ifdef DEBUG
    Serial.println(" (not enough)");
#endif
    return;
  }
    
#ifdef DEBUG
  Serial.print(" (OK)");
#endif

  // A HotWater device may
  // actually run to overflow
  // Arduino's millis counter
  long t = millis();
  
  if(t < lastPressed)
    lastPressed = 0;
    
  if((t - lastPressed) < DEBOUNCE_TIME)
    return;
    
  lastPressed = t;
  
#ifdef DEBUG
  Serial.println("MO pressed");
#endif
    
  // Don't act on no power, except if
  // last run failed
  if(!powerIsOn) {
    if(notifyRunFailed) {
      state = OFF;
      notifyRunFailed = false;
      updateControlPanel();
    }
    
    return;
  }
    
  // Modify state
  switch(state) {
    case OFF:  startManualRun(10); state = MT1; break;
    case MT1:  startManualRun(20); state = MT2; break;
    case MT2:  startManualRun(30); state = MT3; break;
    case MT3:
    case AUTO:
      manualOverrideSegments = getRunSegments(segment);
      state = OFF;
      break;
  }
  // Update display
  updateControlPanel();
}

/**
 *----------------------------------------
 * Starts a manual run for the
 * specified number of minutes,
 * and ensures that scheduled runs
 * through the affected segments
 * are cancelled.
 *----------------------------------------
 */
void startManualRun(int nMinutes) {
  manualRunMinutes = nMinutes;

#ifdef  DEBUG
  manualRunMinutes = nMinutes / 10;
#endif

  int affectedSegments = manualRunMinutes / MINUTES_PER_SEGMENT;
  if(minutes > 0)
    affectedSegments++;
  int lastAffectedSegment = segment + affectedSegments - 1;
  int nextRunSegments = getRunSegments(lastAffectedSegment);
  if(nextRunSegments > 1)
    affectedSegments += nextRunSegments - 1;
  
  manualOverrideSegments = affectedSegments;
}

/**
 *----------------------------------------
 * Updates the control panel display.
 * Valid for all states except when
 * last scheduled run has failed.
 *----------------------------------------
 */
void updateControlPanel() {
  if(lastRunFailed) {
    digitalWrite(heater, OFF);
    return;
  }
    
  int ledPins[] = { manualTimer1, manualTimer2, manualTimer3, automatic };
  int nLeds = 4;
  for(int i = 0; i < nLeds; i++)
    digitalWrite(ledPins[i], LOW);
    
  digitalWrite(heater, (state == OFF) ? LOW : HIGH);
  
  switch(state) {
    case MT1  : digitalWrite(manualTimer1, HIGH); break;
    case MT2  : digitalWrite(manualTimer2, HIGH); break;
    case MT3  : digitalWrite(manualTimer3, HIGH); break;
    case AUTO : digitalWrite(automatic, HIGH); break;
  }
}

/**
 *----------------------------------------
 * Determine the amount of consecutive
 * segments for which the heater will
 * remain on beginning with the specified segment.
 * In other words, determine the length
 * of the current "run", measured in segments.
 *----------------------------------------
 */
int getRunSegments(int startSegment) {
  int runSegments = 0;
  int currentSegment = startSegment;
  int currentHour = hour;
  
  while(true) {
    int currentSchedule = schedule[currentHour];
    if(currentSchedule & (1 << currentSegment))
      runSegments++;
    else
      break;
    currentSegment++;
    if(currentSegment % SEGMENTS_PER_HOUR == 0) {
      currentSegment = 0;
      currentHour++;
      if(currentHour % HOURS_PER_WEEK == 0)
        currentHour = 0;
    }
  }
  
  return runSegments;
}

#ifdef DEBUG
/**
 *----------------------------------------
 * Convenience method to display current
 * week day, hour and segment, for
 * debugging purposes.
 *----------------------------------------
 */
void printTime(int hour, int segment) {
  int weekDay = hour / 24;
  switch(weekDay) {
    case 0 : Serial.print("Sun "); break;
    case 1 : Serial.print("Mon "); break;
    case 2 : Serial.print("Tue "); break;
    case 3 : Serial.print("Wed "); break;
    case 4 : Serial.print("Thu "); break;    
    case 5 : Serial.print("Fri "); break;
    case 6 : Serial.print("Sat "); break;
  }
  int dHour = hour;
  if(segment % SEGMENTS_PER_HOUR == 0) {
    segment = 0;
    dHour++;
  }
  Serial.print(dHour % 24);
  Serial.print(":");
  if(segment == 0)
    Serial.println("00");
  else
    Serial.println(10 * (segment));
}
#endif
