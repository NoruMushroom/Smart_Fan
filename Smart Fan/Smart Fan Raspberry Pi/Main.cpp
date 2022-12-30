#include <iostream>
#include <wiringPi.h>
#include <softPwm.h>
#include "COMMAND.cpp"
#include "CAMERA.cpp"
#include "SERVO.cpp"
#include "FANMOTOR.cpp"
#include "USERVALUE.cpp"
using namespace std;
int main(){
    if(wiringPiSetupGpio() == -1){
        return 0;
    }
    thread Servo(Servo_Ctl);
    thread Cam(Camera);
    exec("sudo ./BLE");
}
