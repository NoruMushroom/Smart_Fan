#include <opencv2/opencv.hpp>
#include <thread>
#include <iostream>
using namespace std;
using namespace cv;
extern int pos;
extern string cam_option;
CascadeClassifier face_cascade;
String face_cascade_name = "/home/pi/Smart_Fan/haarcascade_frontalface_default.xml";
VideoCapture cam(cv::CAP_V4L);
void Camera(){
   if (!face_cascade.load(face_cascade_name)) {
        printf("Check File Path!\n");
        return ;
    }
    cam.set(cv::CAP_PROP_FRAME_WIDTH, 640);
    cam.set(cv::CAP_PROP_FRAME_HEIGHT, 480);
    if (!cam.isOpened()){
        printf("check camera\n");
        return ;
    }
    Mat frame;
    while (cam.read(frame))
    {
        std::vector<Rect> faces;
        Mat frame_gray;
        cvtColor(frame, frame_gray, COLOR_BGR2GRAY);
        equalizeHist(frame_gray, frame_gray);
        face_cascade.detectMultiScale(frame_gray, faces, 1.1, 3, 0 | CASCADE_SCALE_IMAGE, Size(30, 30));
        for (size_t i = 0; i < faces.size(); i++){
            rectangle(frame, Rect(faces[i].x , faces[i].y,faces[i].width, faces[i].height),Scalar(0, 255, 0), 4, 8, 0);
            if(cam_option.compare("Auto") == 0){
                pos = 640 - (faces[i].x + (faces[i].width / 2));
            }
        }
        //std::cout << cam_option << std::endl;
        //imshow("Face", frame);
        char c = (char)waitKey(10);
    	if (c == 27) { break; }
    }
}
