import re
import sys
import os
import json


dayTypes = []
weekTypes = ["DEFAULT"]


def getData(prompt, type, matchRegex = "."):
    while (True):
        answer = input("\n" + prompt + "\n")

        try:
            type(answer)
        except Exception:
            print("Invalid answer")
            continue

        if (not re.match(matchRegex, answer)):
            print("Invalid answer")
            continue

        confirm = input("You answered: " + answer + ". Is this correct?\n[Y]/[n]\n")
        if (confirm != "Y"):
            continue
        else:
            return answer


def welcome():
    print("Welcome to the Period-Countdown School Data Generator")
    print("=====================================================\n")
    print("Data Generation and Usage Process--")
    print("    * Follow the instructions given by this python script and enter information when prompted")
    print("    * Using this script requires:")
    print("        - Basic knowledge of your school's daily bell schedule for each \"type\" of day")
    print("        - Basic knowledge of your school's yearly calendar schedule (including weeks that are different such as 4-day weeks with Monday holidays)\n")
    print("Useful Terminology and Information--")
    print("    * Number - an integer or decimal number, positive or negative (ex: 4, 3.249, -23.0093)")
    print("    * String - a sequence of any characters (ex: \"foo\", \"bar@!.foo*\", \"test123\")")
    print("    * How do you specify a passing period/lunch/other non-class period? - Use the number -1 or -2 for so called \"fake\" periods")
    print("        - -1 indicates to Period-Countdown that the period is very unimportant (ex: passing period). If multiple periods with -1 are places back-to-back the times in them will be merged")
    print("        - -2 indicates to Period-Countdown that the period is somewhat unimportant (ex: lunch, tutorial/studyhall). -2 and -1 periods will not have their times merged")
    print("    * How do you specify times for periods? - Use 24-hour/military time")
    print("        - The entire scope of the day is between 00:00 (the first millisecond of the day) to 23:59 (the last millisecond of the day)\n")
    print("Preface Warning - READ CAREFULLY--")
    print("Before proceeding, please understand and accept that: ")
    print("    1) There is no guarantee Period-Countdown will have an accurate schedule. Always check with teachers and schools about changes in schedules. Do not blame Period-Countdown or its developers for tardies or absences!")
    print("    2) While the Period-Countdown developers try to make it as foolproof as possible, there is no guarantee that all the code will work perfectly. If you encounter issues or have a feature request, please see the Github page online")


def acceptPrefacePrompt():
    acceptPreface = input("\nDo you accept the Preface Warning above?\n[Y]/[n]\n")

    if (acceptPreface != "Y"):
        sys.exit()


def getDays():
    numDayTypes = getData("How many unique day schedules are there in your school's bell schedule (number)", int)

    Days = {}

    for i in range(0, int(numDayTypes)):
        print()
        dayName = getData("Enter a name for the " + str(i + 1) + " day type; can be anything (string)", str)
        numPeriods = getData("Enter the number of periods in the " + dayName + " day (include passing periods, lunch, before/after school, and everything else) (number)", int)

        dayTypes.append(dayName)
        dayDefinition = []

        previousEnd = ""

        for j in range(0, int(numPeriods)):
            periodNumber = getData("Enter the period number for the " + str(j + 1) + " period in the " + dayName + " day (number)", int)
            periodName = getData("Enter the name of the " + str(j + 1) + " period in the " + dayName + " day (string)", str)

            if (previousEnd == "" and j != 0):
                periodStart = getData("Enter the start time in format hh:mm of the " + str(periodName) + " period in the " + dayName + " day (string)", str, "[0-2][0-9]:[0-5][0-9]")
            elif (previousEnd != ""):
                periodStart = previousEnd
            elif (j == 0):
                periodStart = "00:00"

            if (j != (int(numPeriods) - 1)):
                periodEnd = getData("Enter the end time in format hh:mm of the " + str(periodName) + " period in the " + dayName + " day (string)", str, "[0-2][0-9]:[0-5][0-9]")
                previousEnd = periodEnd
            else:
                periodEnd = "23:59"

            periodComment = getData("Enter a comment for the " + str(periodName) + " period in the " + dayName + " day (can be anything, purely for documentation purposes) (string)", str)
            periodEodAdjust = True if (j + 1 == int(numPeriods)) else False

            periodDataDictionary = {"Period": periodNumber, "Name": periodName, "StartTime": periodStart, "EndTime": periodEnd, "Comment": periodComment, "Adjust": periodEodAdjust}
            dayDefinition.append(periodDataDictionary)

        Days[dayName] = dayDefinition

    return Days


def getWeeks():
    print()
    print("Info: Sunday=1 ... Saturday=7")

    Weeks = {}
    defaultWeekDefinition = []

    for i in range(0, 7):
        dayName = getData("For a normal week, enter the name of day " + str(i + 1) + " (can be anything) (string)", str)

        dayType = getData("For a normal week, enter the type of day " + str(i + 1) + " (string)\nThe day type must be one of: " + str(dayTypes), str)
        while (dayType not in dayTypes):
            dayType = getData("For a normal week, enter the type of day " + str(i + 1) + " (string)\nThe day type must be one of: " + str(dayTypes), str)

        weekDataDictionary = {"Name": dayName, "Days": dayType}
        defaultWeekDefinition.append(weekDataDictionary)

    Weeks["DEFAULT"] = defaultWeekDefinition

    numWeekTypes = getData("How many non-default week types are there in the school year (ex: default is ABCDE but Monday holiday is HBCDE, that is 1 non-default) (number)", int)

    for i in range(0, int(numWeekTypes)):
        print()
        weekName = getData("Enter a name for non-default week " + str(i + 1) + " (can be anything) (string)", str)
        weekTypes.append(weekName)

        weekDefinition = []

        for j in range(0, 7):
            dayName = getData("Enter the name of day " + str(j + 1) + " for non-default week " + str(i + 1) + " (can be anything) (string)", str)

            dayType = getData("Enter the type of day " + str(j + 1) + " for non-default week " + str(i + 1) + " (string)\nThe day type must be one of: " + str(dayTypes), str)
            while (dayType not in dayTypes):
                dayType = getData("Enter the type of day " + str(j + 1) + " for non-default week " + str(i + 1) + " (string)\nThe day type must be one of: " + str(dayTypes), str)

            weekDataDictionary = {"Name": dayName, "Days": dayType}
            weekDefinition.append(weekDataDictionary)

        Weeks[weekName] = weekDefinition

    return Weeks


def getWeekExceptions():
    Exceptions = []

    numWeekExceptions = len(weekTypes) - 1

    for i in range(0, numWeekExceptions):
        weekTag = getData("Enter the date of the " + weekTypes[i + 1] + " week exception in format yyyy-mm-dd (string)", str, "[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]")
        weekComment = getData("Enter a comment for the " + weekTypes[i + 1] + " week exception (can be anything) (string)", str)

        weekException = {"WeekTag": weekTag, "Weeks": weekTypes[i + 1], "Comment": weekComment}
        Exceptions.append(weekException)

    return Exceptions


def getInfo():
    firstPeriod = getData("Enter the first possible period of a day (must be >=1) (number)", int)
    lastPeriod = getData("Enter the last possible period of a day (must be >1) (number)", int)
    firstDate = getData("Enter the first day of school in format yyyy-mm-dd (string)", str, "[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]")
    lastDate = getData("Enter the last day of school in format yyyy-mm-dd (string)", str, "[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]")

    Info = {"FirstPeriod": firstPeriod, "LastPeriod": lastPeriod, "FirstDate": firstDate, "LastDate": lastDate}
    return Info


welcome()
acceptPrefacePrompt()
print("\nDAY INFORMATION")
Days = getDays()
print("\nWEEK INFORMATION")
Weeks = getWeeks()
print("\nWEEK EXCEPTIONS")
Exceptions = getWeekExceptions()
print("\nMISC INFORMATION")
Info = getInfo()


jsonPath = os.path.dirname(os.path.dirname(os.path.realpath(__file__))) + "/json/School.json"

read = open(jsonPath, "r")
oldJsonData = read.read()
read.close()
writeOld = open(jsonPath + "~", "w")
writeOld.write(oldJsonData)
writeOld.close()

with open(jsonPath, "w") as outfile:
    json.dump(Days, outfile)
    json.dump(Weeks, outfile)
    json.dump(Exceptions, outfile)
    json.dump(Info, outfile)

print("\n\nProcess complete. Thank you for using Period-Countdown")


# {"User":{"1":{"Period":"Per 1","Name":"Algebra 2","TeacherName":"Migdow","RoomNumber":"414"},"2":{"Period":"Per 2","Name":"CWI","TeacherName":"Block","RoomNumber":"701"},"3":{"Period":"Per 3","Name":"AP Comp","TeacherName":"Newton","RoomNumber":"106"},"4":{"Period":"Per 4","Name":"Chemistry","TeacherName":"Scott","RoomNumber":"116"},"5":{"Period":"Per 5","Name":"Total Fitness","TeacherName":"Kittle","RoomNumber":"gym"},"6":{"Period":"Per 6","Name":"APCS","TeacherName":"Nguyen","RoomNumber":"806"},"7":{"Period":"Per 7","Name":"Spanish 1","TeacherName":"Camarillo","RoomNumber":"607"},"Settings":{"NextUp": 1, "Theme":"ffff11"}}}