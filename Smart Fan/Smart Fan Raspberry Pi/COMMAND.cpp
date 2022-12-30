#include <iostream>
#include <stdexcept>
#include <stdio.h>
#include <string>
#include <algorithm>
#include <sstream>
using namespace std;
extern int pos;
extern int speed;
extern string cam_option;
void exec(string command) {
        char buffer[128];
        FILE* pipe = popen(command.c_str(), "r");

        if (!pipe) {
                cout << "popen failed!";
        }

        while (!feof(pipe)) {
                if (fgets(buffer, 128, pipe) != NULL){
                        string str(buffer);
                        cout << buffer;
                        str.resize(str.size()-1);
                        if (str.compare("Auto") == 0){
                            cam_option = "Auto";
                            speed = 130;
                        }//Auto Mode
                        if(str.compare("Handle") == 0){
                            cam_option = "Handle";
                        }//Handle Mode
                        if(str.compare("Low") == 0){
                            speed = 20;
                        }//Fan
                        if(str.compare("Middle") == 0){
                            speed = 50;
                        }//Fan
                        if(str.compare("High") == 0){
                            speed = 80;
                        }//Fan
                        if(str.compare("Stop") == 0){
                            speed = 100;
                        }//Fan
                        else{
                            stringstream s(str);
                            s >> pos;
                        }//angle
                }
        }
        pclose(pipe);
        return;
}
