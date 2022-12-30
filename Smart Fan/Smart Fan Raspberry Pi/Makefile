#Makefile

CC = g++
TARGET = Main.exe
COMPILE = Main.o
C_option = -lwiringPi `pkg-config opencv4 --cflags --libs`
O_option = libggk.a -lgobject-2.0 -lgio-2.0 -lglib-2.0 -lpthread -lwiringPi `pkg-config opencv4 --cflags --libs`
$(TARGET) : $(COMPILE)
	$(CC) -fPIC -Wall -Wextra -std=c++11 -g -O2  -o Main.exe $(COMPILE) $(O_option)
$(COMPILE) : Main.cpp
	$(CC) -c Main.cpp $(C_option)
clean :
	rm -rf $(COMPILE) $(TARGET)
