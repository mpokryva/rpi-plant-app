#!/usr/bin/env python

# Written by Limor "Ladyada" Fried for Adafruit Industries, (c) 2015
# This code is released into the public domain

import sys
import time
import os
import json
import RPi.GPIO as GPIO
from pubnub import Pubnub

GPIO.setmode(GPIO.BCM)
DEBUG = 1  # set up PubNub subscription, channels, etc.


def set_refresh_rate(new_refresh_rate):
    global refresh_rate
    refresh_rate = new_refresh_rate

class PubnubCustom:
    global pubnubCustom
    global refresh_rate
    
    
    def __init__(self, **kwargs):
        print("init")
        pub_key = kwargs.get('pub_key')
        sub_key = kwargs.get('sub_key')
        channel = kwargs.get('channel')
        refresh_rate = kwargs.get('refresh_rate')
        print (pub_key)
        print (sub_key)
        print(channel)
        print(refresh_rate)
        
        self.pubnub = Pubnub(publish_key=pub_key,
                             subscribe_key=sub_key)  ##'pub-c-442f45b2-dfc6-4df6-97ae-fc0e9efd909a'##'sub-c-6e0344ae-3bd7-11e6-85a4-0619f8945a4f')
        self.channel = channel
        set_refresh_rate(refresh_rate)
     

   


    def publish_callback(self, message):
        print(message)

    @staticmethod
    def _error(message):
        print("ERROR :" + str(message))

    def reconnect(self, message):
        print ("RECONNECTED")

    def disconnect(self, message):
        print("DISCONNECTED")

    def connect(self, message):
        print("CONNECTED")

    def subscribe_callback(self, message, channel):
        parsed_message = json.load(message)
        new_publish_key = parsed_message['publishKey']
        new_subscribe_key = parsed_message['subscribeKey']
        new_channel = parsed_message['channel']
        new_refresh_rate = parsed_message['refreshRate']
        set_refresh_rate(new_refresh_rate)
        global pubnubCustom
        new_settings = make_settings_dict(new_publish_key, new_subscribe_key, new_channel,
                           new_refresh_rate)
                           
        pubnubCustom = PubnubCustom(**new_settings)
        print("Received new settings!")

    def publish(self, **kwargs):
        light_value = kwargs.get('light_value')
        moisture_value = kwargs.get('moisture_value')
        temp_value = kwargs.get('temp_value')
        self.pubnub.publish(pubnubCustom.channel, {'lightValue': light_value,
                                                   'moistureValue' : moisture_value,
                                                   'tempValue' : temp_value},
                                    callback=PubnubCustom.publish_callback,
                                    error=PubnubCustom._error)
        #print ("Published!")
        
 
def make_settings_dict(pub_key, sub_key, channel, refresh_rate):
    settings = {'pub_key':pub_file, 'sub_key' : sub_file, 'channel' : channel_file,
        'refresh_rate':refresh_file}
    return settings          

settings_file = open('PubNub Settings', 'r+')
pub_file = settings_file.readline()
sub_file = settings_file.readline()
channel_file = settings_file.readline()
refresh_file = settings_file.readline()
settings = make_settings_dict(pub_file, sub_file, channel_file, refresh_file)
global pubnubCustom
pubnubCustom = PubnubCustom(**settings)
settings_file.close()

    #pubnubCustom.pubnub.subscribe(channels=pubnubCustom.channel, callback=subscribe_callback,
     #                           connect=connect, reconnect=reconnect,
       #                           disconnect=disconnect)

    #pubnubCustom.pubnub.publish(pubnubCustom.channel, {'lightValue': lightvalue},
    #                         callback=publish_callback,
    #                        error=_error)




# read SPI data from MCP3008 chip, 8 possible adc's (0 thru 7)
def readadc(adcnum, clockpin, mosipin, misopin, cspin):
    if ((adcnum > 7) or (adcnum < 0)):
        return -1
    GPIO.output(cspin, True)

    GPIO.output(clockpin, False)  # start clock low
    GPIO.output(cspin, False)  # bring CS low

    commandout = adcnum
    commandout |= 0x18  # start bit + single-ended bit
    commandout <<= 3  # we only need to send 5 bits here
    for i in range(5):
        if (commandout & 0x80):
            GPIO.output(mosipin, True)
        else:
            GPIO.output(mosipin, False)
        commandout <<= 1
        GPIO.output(clockpin, True)
        GPIO.output(clockpin, False)

    adcout = 0
    # read in one empty bit, one null bit and 10 ADC bits
    for i in range(12):
        GPIO.output(clockpin, True)
        GPIO.output(clockpin, False)
        adcout <<= 1
        if (GPIO.input(misopin)):
            adcout |= 0x1

    GPIO.output(cspin, True)

    adcout >>= 1  # first bit is 'null' so drop it
    return adcout


# change these as desired - they're the pins connected from the
# SPI port on the ADC to the Cobbler
SPICLK = 18
SPIMISO = 23
SPIMOSI = 24
SPICS = 25

# set up the SPI interface pins
GPIO.setup(SPIMOSI, GPIO.OUT)
GPIO.setup(SPIMISO, GPIO.IN)
GPIO.setup(SPICLK, GPIO.OUT)
GPIO.setup(SPICS, GPIO.OUT)

# 10k trim pot connected to adc #0
potentiometer_adc = 0;

last_read = 0  # this keeps track of the last potentiometer value
tolerance = 5  # to keep from being jittery we'll only change
# volume when the pot has moved more than 5 'counts'

while True:
    # we'll assume that the pot didn't move
    trim_pot_changed = False

    # read the analog pin
    trim_pot = readadc(potentiometer_adc, SPICLK, SPIMOSI, SPIMISO, SPICS)
    # how much has it changed since the last read?
    pot_adjust = abs(trim_pot - last_read)
    # voltage, in Volts, of signal
    pot_voltage = trim_pot * (3.3 / 1023)
    light_value = pot_voltage
    moisture_value = 0
    temp_value  = 0

    sensor_values = {'light_value' : light_value,
                     'moisture_value' : moisture_value,
                     'temp_value' : temp_value}

    if DEBUG:
        print "trim_pot:", trim_pot
        
    print "pot_voltage:", pot_voltage
    #                print "pot_adjust:", pot_adjust
    #                print "last_read", last_read
    if pubnubCustom is not None:
        print(pubnubCustom.pubnub.publish_key)
        print(pubnubCustom.channel)
        print(pubnubCustom.pubnub.subscribe_key)
        pubnubCustom.publish(**sensor_values)


    refresh_rate = 5
    # refreshes every refresh_rate secs
    time.sleep(refresh_rate)


