# Hot Water
*Original version of the HotWater/Calenda device design (hardware, firmware, PC software).*

When I picked up the Arduino/electronics hobby back in 2009, my first project was a timer for a tank-based electrical water heater.  These heaters are obnoxious because (1) they must be turned on 5-10 minutes in advance in order to have hot water, and (2) peole have a tendency to leave them on, wasting energy and damaging the heating element.

Back then, there were not many cost-effective alternatives available in town, so I designed an Arduino-based device that I could both program to automatically turn the heater on and off at set times, and that would ensure that any off-schedule runs would finish within an (user-selectable) interval of 10, 20 or 30 minutes.

The device had a very simple UI, and programming it required disconnecting a part from the heater control assembly, plugging that part to the computer, and using a Java program to select the programmed schedule using a UI similar to Microsoft Outlook's calendar view.

The following blog post is the first in a series of four where I describe the HotWater device in great detail:

http://www.modelesis.com/embeddedbliss/2010/01/hotwater-intro/

I evolved the device into a custom-built, single-board solution that included enough UX components to do away with the USB programmable module and the PC software.  However, this project is interesting because it illustrates:

- Serial communications in Java
- A Model/View/Controller (MVC) architecture
- Usage of Java Swing's table UI and table models
