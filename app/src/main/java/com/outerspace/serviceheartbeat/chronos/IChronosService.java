package com.outerspace.serviceheartbeat.chronos;

public interface IChronosService {

    public void addClient(IChronosClient client);

    public void removeClient(IChronosClient client);

    public boolean isBound();

    public void pingService();

    public void terminateService();

}
