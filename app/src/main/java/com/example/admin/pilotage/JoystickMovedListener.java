package com.example.admin.pilotage;

/*This is a 100% abstract class, OnMoved is called when the joystick is moved, OnReleased when the
joystick is released, the content of this functions is set at the instantiation of a new joystick*/
public interface JoystickMovedListener {
    public void OnMoved(double x, double y);
    public void OnReleased();
}