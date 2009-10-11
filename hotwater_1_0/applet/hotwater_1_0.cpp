/*
 * HotWater Firmware v1.0
 * (c) 2009 Ram\u00f3n Jim\u00e9nez
 */
 
// Debug flag - comment out for production
#define  DEBUG  1
 
// Pin definitions
#include "WProgram.h"
void setup();
void loop();
void waitASecond();
void updateTime();
void updateStateOnMinuteChange();
void updateStateOnSegmentChange();
void moPressed();
void startManualRun(int nMinutes);
void updateControlPanel();
int getRunSegments(int startSegment);
void printTime(int hour, int segment);
const int manualOverride  = 3;
const int manualTimer1    = 4;
const int manualTimer2    = 5;
const int manualTimer3    = 6;
const int automatic       = 7;
const int heater          = 8;
const int acPower         = 9;

// State definitions
#define  OFF     0
#define  AUTO    1
#define  MT1     2
#define  MT2     3
#define  MT3     4
#define  FAILED  5

// Time unit definitions
#define  HOURS_PER_WEEK     168
#ifdef DEBUG
#define  SECONDS_PER_MINUTE  1
#define  MINUTES_PER_SEGMENT 1
#else
#define  SECONDS_PER_MINUTE  60
#define  MINUTES_PER_SEGMENT 10
#endif
#define  SEGMENTS_PER_HOUR  6

// Heater state
int state = OFF;

// Manual run minutes
int manualRunMinutes = 0;

// Manual override segments -
// for how many segments should
// automatic scheduled runs be
// inhibited because of a manual
// override run or cancellation?
int manualOverrideSegments = 0;

// Schedule
int schedule[HOURS_PER_WEEK];

// Time control vars
int seconds = 0;
int minutes = 0;
int segment = 0;
int hour = 0;

/**
 * Firmware initialization.
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
#ifdef DEBUG
  Serial.begin(9600);
  //
  for(int z = 0; z < HOURS_PER_WEEK; z++)
    schedule[z] = 0;
  schedule[1] = B00111111;  // Sunday, 1:00 AM thru 1:30 AM
  schedule[2] = B00110000;  // Sunday, 2:40 AM thru 3:00 AM
#endif
}

/**
 * Main loop.
 */
void loop() {
  updateTime();
  updateControlPanel();
  waitASecond();
}

/**
 * Time counting function.
 * Also does some power management.
 */
void waitASecond() {
  delay(1000);
}

/**
 * Updates time variables.
 */
void updateTime() {
  // Update time
  seconds++;
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
        if(hour == HOURS_PER_WEEK)
          hour = 0; // The week has changed
      } // Segment
      
      updateStateOnSegmentChange();
    } // Minute
    
    if(manualOverrideSegments == 0)
      updateStateOnMinuteChange();
  } // Second
}
  
/**
 * Performs any state changes as required
 * by the fact that a minute has elapsed.
 */
void updateStateOnMinuteChange() {
  // Update state as required:
  //
  // * Turn off if on-auto, on-auto if off as applicable
  // * If manually timed, check whether elapsed time is up
  //   to turn off, otherwise leave as is (MTn state)
  if((state == OFF) || (state == AUTO)) {
    int currentSchedule = schedule[hour];
    if(currentSchedule & (1 << segment))
      state = AUTO;
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
 * Performs any state changes as required
 * by the fact that a minute has elapsed.
 * This is only used to enforce scheduled
 * runs override by manual runs - the
 * updateStateOnMinuteChange() method handles
 * segment transitions as well.
 */
void updateStateOnSegmentChange() {
  if(manualOverrideSegments > 0)
    manualOverrideSegments--;
}

/**
 * Manual override pressed
 * interrupt handler.
 */
void moPressed() {
#ifdef DEBUG
  Serial.println("MO pressed");
#endif
    
  // Modify state
  switch(state) {
    case OFF:  startManualRun(10); state = MT1; break;
    case MT1:  startManualRun(20); state = MT2; break;
    case MT2:  startManualRun(30); state = MT3; break;
    default:
      manualOverrideSegments = getRunSegments(segment);
      state = OFF;
      break;
  }
  // Update display
  updateControlPanel();
}

/**
 * Starts a manual run for the
 * specified number of minutes,
 * and ensures that scheduled runs
 * through the affected segments
 * are cancelled.
 */
void startManualRun(int nMinutes) {
  manualRunMinutes = nMinutes;

#ifdef  DEBUG
  manualRunMinutes = nMinutes / 10;
#endif

  int affectedSegments = manualRunMinutes / MINUTES_PER_SEGMENT;
  if(minutes > 0)
    affectedSegments++;
  for(int i = 0; i < affectedSegments; i++) {
    int runSegments = getRunSegments(segment + i);
    if(runSegments > 0) {
      affectedSegments += runSegments;
      break;
    }
  }
  manualOverrideSegments = affectedSegments;
}

/**
 * Updates the control panel display.
 * Valid for all states except FAILED.
 */
void updateControlPanel() {
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
 * Determine the amount of consecutive
 * segments for which the heater will
 * remain on beginning with the specified segment.
 * In other words, determine the length
 * of the current "run", measured in segments.
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

int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

