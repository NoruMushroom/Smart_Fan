#include <wiringPi.h>
#include <softPwm.h>
#define SERVO 13 //GPIO NUM
extern void Fan_Ctl();
void Servo_Ctl(){
    if(wiringPiSetupGpio() == -1){
        return;
    }
    softPwmCreate(SERVO, 0, 200);
    thread Fan(Fan_Ctl);
    while(true){
        if(cam_option.compare("Handle") == 0){
           softPwmWrite(SERVO,(pos/9) + 5);
	}
        if(cam_option.compare("Auto") == 0){
            softPwmWrite(SERVO,(pos/32) + 5);
        }
        delay(100);
    }
}
