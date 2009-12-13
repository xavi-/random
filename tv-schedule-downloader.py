import urllib2
import datetime

def getSchedule(date, windowMinutes = 120):
    url = """http://www.tvguide.com/listings/data/ajaxdata.ashx?"""
    query = """srvid=80001&gridmins=%d&gridyr=%d&gridmo=%d&griddy=%d&gridhr=%d"""
        
    parms = (windowMinutes, date.year, date.month, date.day, date.hour)

    return urllib2.urlopen(url + query % parms).readlines()[1:];
    
class episode:
   # Parses a date in this form: YYYYMMDDHHMM
    def __parseDate(self, rawform):
        year = int(rawform[0:4])
        month = int(rawform[4:6])
        day = int(rawform[6:8])
        hour = int(rawform[8:10])
        minute = int(rawform[10:12])
        
        return datetime.datetime(year, month, day, hour, minute)     
    def __init__(self, rawForm):
        self.orginalForm = rawForm
        
        rawForm = filter(lambda x: x != "", rawForm.split("\t"))
        self.channel = int(rawForm[0])
        self.channelName = rawForm[1]
        
        self.title = rawForm[3]
        self.dateTime = self.__parseDate(rawForm[-4])
        self.duration = int(rawForm[-3])
   
#def parse
episodes = []
for rawEpisode in getSchedule(datetime.datetime.now()):
    episodes.append(episode(rawEpisode))
    
channels = {}
for episode in episodes:
    channel = (episode.channel, episode.channelName)
    
    if channel not in channels:
        channels[channel] = []
    
    channels[channel].append(episode)
    channels[channel].sort(lambda x, y: [-1, 1][x.dateTime > y.dateTime])

for channel in sorted(channels.keys()):
    print "Channel: %d %s" % channel
    
    for episode in channels[channel]:
        time = episode.dateTime
        
        print "\t%s @ %d:%02d %s" % (episode.title, time.hour % 12, 
                                      time.minute, ["am", "pm"][time.hour > 11])

