#define FAN 12 //GPIO NUM
void Fan_Ctl(){
    if(wiringPiSetupGpio() == -1){
        return;
    }
    softPwmCreate(FAN, 0, 100);
    while(true){
        softPwmWrite(FAN,speed); //stop
        delay(100);
    }
}
