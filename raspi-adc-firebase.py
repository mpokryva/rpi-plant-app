#!/usr/bin/env python

# Written by Limor "Ladyada" Fried for Adafruit Industries, (c) 2015
# This code is released into the public domain

import sys
import time
import os
import json
import RPi.GPIO as GPIO
from firebase import firebase
import requests
import Queue
import threading
from waiting import wait, TimeoutExpired 

GPIO.setmode(GPIO.BCM)
DEBUG = 1  # set up PubNub subscription, channels, etc.
firebase_path = 'https://rpiplantsapp.firebaseio.com'
firebase_root = firebase.FirebaseApplication(firebase_path,
                                        None)

firebase_modified_plants = firebase.FirebaseApplication(firebase_path +
                                                        '/modifiedPlants',
                                                        None)


        
result = firebase_root.get('/settings', 'refreshRate')
print result


def set_refresh_rate(new_refresh_rate):
    global refresh_rate
    refresh_rate = new_refresh_rate




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
def start_sensors():
    GPIO.setwarnings(False)
    while True:
        # we'll assume that the pot didn't move
        trim_pot_changed = False

        # read the analog pin
        trim_pot = readadc(potentiometer_adc, SPICLK, SPIMOSI, SPIMISO, SPICS)
        # how much has it changed since the last read?
        pot_adjust = abs(trim_pot - last_read)
        # voltage, in Volts, of signal
        pot_voltage = trim_pot * (3.3 / 1023)
        light_value = trim_pot
        moisture_value = 0
        temp_value  = 0

        sensor_values = {'light_value' : light_value,
                         'moisture_value' : moisture_value,
                         'temp_value' : temp_value}
        firebase_root.put(firebase_path, '/sensorResults', sensor_values)

        if DEBUG:
            print "trim_pot:", trim_pot
            
        print "pot_voltage:", pot_voltage
        #                print "pot_adjust:", pot_adjust
        #                print "last_read", last_read
        # refreshes every refresh_rate secs
        refresh_rate = firebase_root.get('/settings', 'refreshRate')
        print("Starting check_for_plants()")
        check_for_plants(refresh_rate)
      
       

def check_for_plants(refresh_rate):
    print ("Checking for plants")
    seconds_slept = 0
    while (seconds_slept < refresh_rate):
        modded_plants = firebase_root.get('/modifiedPlants', '')
        print(modded_plants)
        if modded_plants is not None:
            for plant, properties in modded_plants.items():
                print properties['status']
                plants_updated = []
                plants_deleted = []
                if properties['status'] == 'updated':
                    plants_updated.append(plant)   
                elif properties['status'] == 'deleted':
                    plants_deleted.append(plant)
                firebase_root.delete('/modifiedPlants', '/' + plant)
            if (len(plants_updated)==0 and len(plants_deleted)==0):
                seconds_slept += 2
                time.sleep(2)
            else:
                for plant in plants_deleted:
                    firebase_root.delete('/userPlants', '/' + plant)
                start_sensors()
        else:
            seconds_slept += 2
            time.sleep(2)


start_sensors()
