/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.esper;

public class SensorEvent
{

    private int temperature;
    private String sensorName;

    public SensorEvent(int temperature, String sensorName)
    {
        this.temperature = temperature;
        this.sensorName = sensorName;
    }

    public String getSensorName()
    {
        return sensorName;
    }

    public void setSensorName(String sensorName)
    {
        this.sensorName = sensorName;
    }

    public int getTemperature()
    {
        return temperature;
    }

    public void setTemperature(int temperature)
    {
        this.temperature = temperature;
    }
}
