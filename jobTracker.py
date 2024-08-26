# importing libraries
import matplotlib.pyplot as plt
from datetime import datetime, timedelta
import os.path, sys
import requests, time
from bs4 import BeautifulSoup
import numpy
import statistics
import sys

plt.style.use("dark_background")


# creating array of dates for x axis
def getJobsFromSite(jobTitle):
    url = f"https://www.linkedin.com/jobs/search?keywords={jobTitle}&location=United%20States&geoId=103644278&f_E=2&f_TPR=r86400&position=1&pageNum=0&original_referer=https%3A%2F%2Fwww.linkedin.com%2Fjobs%2Fsearch%3Fkeywords%3DSoftware%26location%3DUnited%2520States%26geoId%3D103644278%26f_TPR%3D%26f_E%3D2%26position%3D1%26pageNum%3D0"
    time.sleep(10)
    sys.exit(-1)
    print("trying to get to url", url)
    response = requests.get(url)

    if response.status_code == 200:
        # Parse the HTML content using Beautiful Soup
        soup = BeautifulSoup(response.text, "html.parser")

        # Extract the text from a specific element (e.g., all <p> tags)
        paragraphs = soup.find_all("label")
        for p in paragraphs:
            if ("Entry level (" in p.text):
                print(p.text)
                number = p.text[p.text.find("(") + 1:p.text.find(")")].replace(",", "")
                return (int(number))

    else:

        print("Failed to retrieve the webpage. Status code:", response.status_code)

        return -1


def plotPointsForJob(jobTitle, scrape=True, byweek=True):
    filePath = os.path.join(sys.path[0], f"./searches/{jobTitle}.search")
    with open(filePath, "a") as file:
        pass
    # Read the file content
    with open(filePath, "r") as file:
        lines = file.read().splitlines()

    last_line = ""
    if len(lines) > 0:
        last_line = lines[-1]
    if (scrape):

        if last_line.split(":")[0] == str(datetime.now().date()):
            lines = lines[:-1]

        with open(filePath, "w") as file:
            file.writelines(line + "\n" for line in lines)
        with open(filePath, "a") as file:
            if (len(lines) == 0):
                yesterday = datetime.now() - timedelta(days=1)
                file.write(f"{yesterday.date()}:{0}\n")
            value = getJobsFromSite(jobTitle);
            if value != -1:
                file.write(f"{datetime.now().date()}:{value}\n")
    dates = []
    x = []

    with open(filePath, "r") as file:
        lines = file.readlines()
        byWeekCounter = 0
        for line in lines:
            date = line.split(":")[0]
            jobs = line.split(":")[1]
            if (byweek):
                index = int((byWeekCounter / 7))
                print(index, jobs)

                if len(x) <= index:
                    x.extend([0] * (index + 1 - len(x)))
                x[index] += int(jobs)
                if (byWeekCounter % 7 == 0):
                    dates.append(date)
            else:
                dates.append(date)
                x.append(int(jobs))
            byWeekCounter += 1

    averages = []
    rollingAverages = []
    rollingSum = 0
    rollingMedian = [x[0]] * len(x)
    # print(type()
    for i, num in enumerate(x):
        rollingSum += num;
        rollingMedian[i] = rollingSum

        rollingAverages.append(rollingSum / (i + 1))
    xAverage = numpy.mean(x)
    xMedian = statistics.median(x)
    print(datetime.strptime(dates[0], '%Y-%m-%d').weekday())
    # Create a new list where every element is the average
    averages = [xAverage] * len(x)
    median = [xMedian] * len(x)

    plt.plot(dates, x, label=jobTitle, marker="o")
    # plt.plot(dates, averages, label =jobTitle + " Average", linestyle ='dashed')
    plt.plot(dates, rollingAverages, label=jobTitle + " Rolling Average", linestyle="dashed", marker="o")
    # plt.plot(dates, median, label =jobTitle + " Median", marker ="o")
    if (x[len(x) - 1] > xAverage):
        print("Today is higher than average")
    plt.xticks(rotation=70)


plotPointsForJob("Software Developer", False, False)

plt.legend()
plt.show()
