package jconsole;
/*
 *   By: Adrian Gonzalez Madruga @ 10/21/2016 Version 1.1
 */

import java.util.ArrayList;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class JConsole {
    
// <editor-fold defaultstate="collapsed" desc="Variables">
    private ArrayList<Controller> foundControllers = new ArrayList<>();
    private ArrayList<Integer> buttonPos = new ArrayList<>();
    private ArrayList<Integer> AnalogPosX = new ArrayList<>();
    private ArrayList<Integer> AnalogPosY = new ArrayList<>();
    private int DpadLocation;
    private double DpadDirection[][] = {{.125, .25, .375}, {.625, .75, .875}, {.875, 1.0, .125}, {.375, .5, .625}};//[direction][all that apply]
    private String direction[] = {"up", "down", "left", "right"};
    private int buttonOrganizer = 0;
    private int AnalogOrganizerX = 0;
    private int AnalogOrganizerY = 0;
    private int controllerOrganizer = 0;
    private int c = 0;
    private boolean on = true;
    // </editor-fold>

    public JConsole() {
        findController();
    }

    public JConsole(int c) {
        this.c = c;
        findController();
    }

    public void findController() { //get all controller types known to Jinput that are connected and store them
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for (int q = 0; q < controllers.length; q++) { // for each connected verified controller
            Controller controller = controllers[q];
            if (controller.getType() == Controller.Type.STICK || controller.getType() == Controller.Type.GAMEPAD || controller.getType() == Controller.Type.WHEEL || controller.getType() == Controller.Type.FINGERSTICK) { // if one of these types of controller
                foundControllers.add(controllerOrganizer, controller); // add this to the list of valid controllers to use
                controllerOrganizer++;
            }
        }
        controllerOrganizer = 0;
        if (!foundControllers.isEmpty() || (foundControllers.size() - 1) <= c) {
            sortController();
        } else {
            on = false;
        }
    }

    public void sortController() {//get the component slot number and sort it into each button, dpad, and analog
        int n = 2;
        Controller controller = foundControllers.get(c); // get the proper controller that is being analyzed
        Component[] components = controller.getComponents(); // get a list of the component numbers in the device
        for (int e = 0; e < components.length; e++) {
            Component component = components[e];
            Identifier componentIdentifier = component.getIdentifier(); //get this components id
            if (componentIdentifier.getName().matches("^[0-9]*$")) { //if a button get it's component number
                buttonPos.add(buttonOrganizer, e);
                buttonOrganizer++;
                continue;
            }
            if (componentIdentifier == Component.Identifier.Axis.POV) { //if a dpad get it's component number
                DpadLocation = e;
                continue;
            }
            if (component.isAnalog()) { //if an analog get it's component number
                if (componentIdentifier == Component.Identifier.Axis.X) { //sort between x and y
                    AnalogPosX.add(AnalogOrganizerX, e);
                    AnalogOrganizerX++;
                    n++;
                    continue;
                }
                if (componentIdentifier == Component.Identifier.Axis.Y) { //sort between x and y
                    AnalogPosY.add(AnalogOrganizerY, e);
                    AnalogOrganizerY++;
                    n++;
                    continue;
                }
                if (n % 2 == 0) { // any excess analogs will be the right analog stick(1) or the shoulder bumpers(they will not work in this series)
                    AnalogPosY.add(AnalogOrganizerY, e);
                    AnalogOrganizerY++;
                    n++;
                } else if (n % 2 == 1) {
                    AnalogPosX.add(AnalogOrganizerX, e);
                    AnalogOrganizerX++;
                    n++;
                }
            }
        }
        buttonOrganizer = 0;
        AnalogOrganizerY = 0;
        AnalogOrganizerX = 0;
    }

    public boolean isButtonPressed(int n) { //get the button slot then ask if being pressed
        boolean isItPressed = false;
        if (on) {
            Controller controller = foundControllers.get(c);
            controller.poll();
            Component[] components = controller.getComponents();
            Component component = components[buttonPos.get(n)];
            if (component.getPollData() != 0.0f) { //if pressed == 1.0
                isItPressed = true;
            }
        }
        return isItPressed; //return is pressed
    }

    public boolean isDpadPressed(String dir) { //ask which direction is wanted then say if that direction is being outputted (asking in a string as up down left right)
        boolean isItPressed = false;
        if (on) {
            Controller controller = foundControllers.get(c);
            controller.poll();
            Component[] components = controller.getComponents();
            Component component = components[DpadLocation];
            for (int q = 0; q < 4; q++) {
                if (dir.equalsIgnoreCase(direction[q])) { //if the string is "up, down, left, right" then output appropriate direction
                    if (component.getPollData() == DpadDirection[q][0] || component.getPollData() == DpadDirection[q][1] || component.getPollData() == DpadDirection[q][2]) {
                        isItPressed = true;
                    }
                }
            }
        }
        return isItPressed;
    }

    public boolean isDpadPressed(int n) { //ask which direction is wanted then say if that direction is being outputted
        boolean isItPressed = false;
        if (on) {
            Controller controller = foundControllers.get(c);
            controller.poll();
            Component[] components = controller.getComponents();
            Component component = components[DpadLocation];
            if (component.getPollData() == DpadDirection[n][0] || component.getPollData() == DpadDirection[n][1] || component.getPollData() == DpadDirection[n][2]) {
                isItPressed = true;
            }
        }
        return isItPressed;
    }

    public int analogHorizontal() { // left analog stick X Component
        if (on) {
            Controller controller = foundControllers.get(c);
            controller.poll();
            Component[] components = controller.getComponents();
            Component component = components[AnalogPosX.get(0)];
            double axisValue = component.getPollData();
            int axisValuePercentage = getAxisValuePercentage(axisValue); //get the value between 0 and 100 (left-right)
            return axisValuePercentage;
        } else {
            return 50;
        }
    }

    public int analogHorizontal(int n) { // left analog stick X Component
        if (on) {
            Controller controller = foundControllers.get(c);
            controller.poll();
            Component[] components = controller.getComponents();
            Component component = components[AnalogPosX.get(n)];
            double axisValue = component.getPollData();
            int axisValuePercentage = getAxisValuePercentage(axisValue); //get the value between 0 and 100 (left-right)
            return axisValuePercentage;
        } else {
            return 50;
        }
    }

    public int analogVertical() { // left analog stick Y Component
        if (on) {
            Controller controller = foundControllers.get(c);
            controller.poll();
            Component[] components = controller.getComponents();
            Component component = components[AnalogPosY.get(0)];
            double axisValue = component.getPollData();
            int axisValuePercentage = getAxisValuePercentage(axisValue); //get the value between 0 and 100 (up-down)
            return axisValuePercentage;
        } else {
            return 50;
        }
    }

    public int analogVertical(int n) { // left analog stick Y Component
        if (on) {
            Controller controller = foundControllers.get(c);
            controller.poll();
            Component[] components = controller.getComponents();
            Component component = components[AnalogPosY.get(n)];
            double axisValue = component.getPollData();
            int axisValuePercentage = getAxisValuePercentage(axisValue); //get the value between 0 and 100 (up-down)
            return axisValuePercentage;
        } else {
            return 50;
        }
    }

    public int getAxisValuePercentage(double axisValue) { // find the percentage of the analog sticks
        return (int) (((2 - (1 - axisValue)) * 100) / 2);
    }

    public void findOutput() { //if a button is pressed output what button it is (same with Dpad)
        if (on) {
            String Output = "Output of -" + foundControllers.get(c).getName() + "- : ";
            boolean pressed = false;
            for (int q = 0; q < buttonPos.size(); q++) {
                if (isButtonPressed(q)) { //get if that button is pressed
                    Output = (Output + " Button " + q + " isPressed.");
                    pressed = true;
                }
            }
            String[] dir = {"up", "down", "left", "right"};
            for (int q = 0; q < 4; q++) { // get if that Dpad direction is pressed
                if (isDpadPressed(q)) {
                    Output = (Output + " Dpad is moving " + dir[q] + ".");
                    pressed = true;
                }
            }
            if (pressed) { // if something is actually being pressed
                System.out.println(Output);
            }
        } else {
            System.out.println("No Controller Found");
        }
    }
}