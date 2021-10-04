def pad(toPad):
    if (toPad <= 9):
        return "0" + str(toPad)
    else:
        return toPad

PER = 1

for hours in range(24):
    for minutes in range(60):
        start = str(pad(hours)) + ":" + str(pad(minutes))
        end = str(pad(hours)) + ":" + str(pad(minutes + 1))

        if (minutes == 59 and hours != 23):
            end = str(pad(hours + 1)) + ":00"

        PER += 1
        if (PER > 7):
            PER = 1

        ELEM = {"Period": PER, "Name": "N", "StartTime": start, "EndTime": end, "Comment": "", "Adjust": False}
        print(str(ELEM) + ",")
