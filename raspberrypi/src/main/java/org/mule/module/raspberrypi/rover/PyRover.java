package org.mule.module.raspberrypi.rover;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class PyRover implements Rover
{


    private GpioController gpio;
    private GpioPinDigitalOutput goLeftEngine;
    private GpioPinDigitalOutput goRightEngine;
    private GpioPinDigitalOutput backLeftEngine;
    private GpioPinDigitalOutput backRightEngine;


    private Direction currentDirection;

    private boolean engineRunning = false;


    public PyRover()
    {
    }

    @Override
    public synchronized void startEngine()
    {
        if (!engineRunning)
        {
            System.out.println("Starting rover");

            // create gpio controller
            gpio = GpioFactory.getInstance();
            // provision gpio pin #01 as an output pin and turn when

            goLeftEngine = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "GoLeft", PinState.LOW);
            goRightEngine = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "GoRight", PinState.LOW);
            backLeftEngine = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "BackLeft", PinState.LOW);
            backRightEngine = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "BackRight", PinState.LOW);

            stop();
            engineRunning = true;
        }
    }


    @Override
    public void forward()
    {
        //Left Part
        goLeftEngine.setState(PinState.HIGH);
        backLeftEngine.setState(PinState.LOW);

        //Right Part
        goRightEngine.setState(PinState.HIGH);
        backRightEngine.setState(PinState.LOW);

        setCurrentDirection(Direction.FORWARD);

    }

    @Override
    public void rotateLeft()
    {
        goLeftEngine.setState(PinState.LOW);
        backLeftEngine.setState(PinState.HIGH);


        goRightEngine.setState(PinState.HIGH);
        backRightEngine.setState(PinState.LOW);
        setCurrentDirection(Direction.LEFT);
    }

    @Override
    public void rotateRight()
    {
        goLeftEngine.setState(PinState.HIGH);
        backLeftEngine.setState(PinState.LOW);

        goRightEngine.setState(PinState.LOW);
        backRightEngine.setState(PinState.HIGH);
        setCurrentDirection(Direction.RIGHT);
    }

    @Override
    public void backwards()
    {
        goLeftEngine.setState(PinState.LOW);
        backLeftEngine.setState(PinState.HIGH);

        goRightEngine.setState(PinState.LOW);
        backRightEngine.setState(PinState.HIGH);
        setCurrentDirection(Direction.BACKWARDS);
    }


    @Override
    public void stop()
    {
        goLeftEngine.setState(PinState.LOW);
        backLeftEngine.setState(PinState.LOW);

        goRightEngine.setState(PinState.LOW);
        backRightEngine.setState(PinState.LOW);

        setCurrentDirection(Direction.STOP);

    }


    @Override
    public Direction getCurrentDirection()
    {
        return currentDirection;
    }


    @Override
    public synchronized void stopEngine()
    {
        if (engineRunning)
        {
            stop();
            gpio.shutdown();
            engineRunning = false;
        }
    }

    private void setCurrentDirection(Direction currentDirection)
    {
        this.currentDirection = currentDirection;
    }
}