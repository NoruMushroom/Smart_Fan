#include <signal.h>
#include <iostream>
#include <thread>
#include <sstream>
#include "Gobbledegook.h"

//
// Constants
//

// Maximum time to wait for any single async process to timeout during initialization
static const int kMaxAsyncInitTimeoutMS = 30 * 1000;

//
// Server data values
//

// The battery level ("battery/level") reported by the server (see Server.cpp)
static uint8_t serverDataBatteryLevel = 78;

// The text string ("text/string") used by our custom text string service (see Server.cpp)
static std::string serverDataTextString = "Hello, world!";

//
// Logging
//

enum LogLevel
{
	Debug,
	Verbose,
	Normal,
	ErrorsOnly
};

// Our log level - defaulted to 'Normal' but can be modified via command-line options
LogLevel logLevel = Normal;

// Our full set of logging methods (we just log to stdout)
//
// NOTE: Some methods will only log if the appropriate `logLevel` is set
void LogDebug(const char *pText) { if (logLevel <= Debug) { std::cout << "  DEBUG: " << pText << std::endl; } }
void LogInfo(const char *pText) { if (logLevel <= Verbose) { std::cout << "   INFO: " << pText << std::endl; } }
void LogStatus(const char *pText) { if (logLevel <= Normal) { std::cout << " STATUS: " << pText << std::endl; } }
void LogWarn(const char *pText) { std::cout << "WARNING: " << pText << std::endl; }
void LogError(const char *pText) { std::cout << "!!ERROR: " << pText << std::endl; }
void LogFatal(const char *pText) { std::cout << "**FATAL: " << pText << std::endl; }
void LogAlways(const char *pText) { std::cout << "..Log..: " << pText << std::endl; }
void LogTrace(const char *pText) { std::cout << "-Trace-: " << pText << std::endl; }

//
// Signal handling
//

// We setup a couple Unix signals to perform graceful shutdown in the case of SIGTERM or get an SIGING (CTRL-C)
void signalHandler(int signum)
{
	switch (signum)
	{
		case SIGINT:
			LogStatus("SIGINT recieved, shutting down");
			ggkTriggerShutdown();
			break;
		case SIGTERM:
			LogStatus("SIGTERM recieved, shutting down");
			ggkTriggerShutdown();
			break;
	}
}

//
// Server data management
//

// Called by the server when it wants to retrieve a named value
//
// This method conforms to `GGKServerDataGetter` and is passed to the server via our call to `ggkStart()`.
//
// The server calls this method from its own thread, so we must ensure our implementation is thread-safe. In our case, we're simply
// sending over stored values, so we don't need to take any additional steps to ensure thread-safety.
const void *dataGetter(const char *pName)
{
	if (nullptr == pName)
	{
		LogError("NULL name sent to server data getter");
		return nullptr;
	}

	std::string strName = pName;

	if (strName == "battery/level")
	{
		return &serverDataBatteryLevel;
	}
	else if (strName == "text/string")
	{
		return serverDataTextString.c_str();
	}

	LogWarn((std::string("Unknown name for server data getter request: '") + pName + "'").c_str());
	return nullptr;
}

// Called by the server when it wants to update a named value
//
// This method conforms to `GGKServerDataSetter` and is passed to the server via our call to `ggkStart()`.
//
// The server calls this method from its own thread, so we must ensure our implementation is thread-safe. In our case, we're simply
// sending over stored values, so we don't need to take any additional steps to ensure thread-safety.
int dataSetter(const char *pName, const void *pData)
{
	if (nullptr == pName)
	{
		LogError("NULL name sent to server data setter");
		return 0;
	}
	if (nullptr == pData)
	{
		LogError("NULL pData sent to server data setter");
		return 0;
	}

	std::string strName = pName;

	if (strName == "battery/level")
	{
		serverDataBatteryLevel = *static_cast<const uint8_t *>(pData);
		LogDebug((std::string("Server data: battery level set to ") + std::to_string(serverDataBatteryLevel)).c_str());
		return 1;
	}
	else if (strName == "text/string")
	{
		serverDataTextString = static_cast<const char *>(pData);
		std::cout<<serverDataTextString<<std::endl;
		return 1;
	}

	LogWarn((std::string("Unknown name for server data setter request: '") + pName + "'").c_str());

	return 0;
}

//
// Entry point
//

int main(int argc, char **ppArgv)
{
	// A basic command-line parser
	for (int i = 1; i < argc; ++i)
	{
		std::string arg = ppArgv[i];
		if (arg == "-q")
		{
			logLevel = ErrorsOnly;
		}
		else if (arg == "-v")
		{
			logLevel = Verbose;
		}
		else if  (arg == "-d")
		{
			logLevel = Debug;
		}
		else
		{
			LogFatal((std::string("Unknown parameter: '") + arg + "'").c_str());
			LogFatal("");
			LogFatal("Usage: standalone [-q | -v | -d]");
			return -1;
		}
	}

	// Setup our signal handlers
	signal(SIGINT, signalHandler);
	signal(SIGTERM, signalHandler);

	// Register our loggers
	ggkLogRegisterDebug(LogDebug);
	ggkLogRegisterInfo(LogInfo);
	ggkLogRegisterStatus(LogStatus);
	ggkLogRegisterWarn(LogWarn);
	ggkLogRegisterError(LogError);
	ggkLogRegisterFatal(LogFatal);
	ggkLogRegisterAlways(LogAlways);
	ggkLogRegisterTrace(LogTrace);

	// Start the server's ascync processing
	//
	// This starts the server on a thread and begins the initialization process
	//
	// !!!IMPORTANT!!!
	//
	//     This first parameter (the service name) must match tha name configured in the D-Bus permissions. See the Readme.md file
	//     for more information.
	//
	if (!ggkStart("gobbledegook", "Gobbledegook", "Smart Fan", dataGetter, dataSetter, kMaxAsyncInitTimeoutMS))
	{
		return -1;
	}

	// Wait for the server to start the shutdown process
	//
	// While we wait, every 15 ticks, drop the battery level by one percent until we reach 0
	while (ggkGetServerRunState() < EStopping)
	{
		std::this_thread::sleep_for(std::chrono::seconds(15));

		serverDataBatteryLevel = std::max(serverDataBatteryLevel - 1, 0);
		ggkNofifyUpdatedCharacteristic("/com/gobbledegook/battery/level");
	}

	// Wait for the server to come to a complete stop (CTRL-C from the command line)
	if (!ggkWait())
	{
		return -1;
	}

	// Return the final server health status as a success (0) or error (-1)
  	return ggkGetServerHealth() == EOk ? 0 : 1;
}
