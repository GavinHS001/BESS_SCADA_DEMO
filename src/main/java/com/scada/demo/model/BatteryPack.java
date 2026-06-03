package com.scada.demo.model;

public class BatteryPack {
    private int id;
    private double soc;       // %
    private double voltage;   // V (高压系统 1500V-1800V)
    private double current;   // A
    private double power;     // W

    public BatteryPack(int id, double soc, double voltage) {
        this.id = id;
        this.soc = soc;
        this.voltage = voltage;
        this.current = 0.0;
        this.power = 0.0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public double getSoc() { return soc; }
    public void setSoc(double soc) { this.soc = Math.max(0, Math.min(100, soc)); }
    public double getVoltage() { return voltage; }
    public void setVoltage(double voltage) { this.voltage = voltage; }
    public double getCurrent() { return current; }
    public void setCurrent(double current) { this.current = current; }
    public double getPower() { return power; }
    public void setPower(double power) { this.power = power; }
}
