package com.outerspace.serviceheartbeat.chronos;

public interface IChronosClient {
    public void setName(String name);
    public String getName();

    public void setInterval(Long millSecs);
    public Long getInterval();

    public void enable(boolean enabled);
    public boolean isEnabled();

    public abstract void chronosResponse(String response);

}
