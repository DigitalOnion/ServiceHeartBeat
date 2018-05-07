package com.outerspace.serviceheartbeat.chronos;

public interface IChronosService {

    public void addClient(IChronosClient client);

    public void removeClient(IChronosClient client);

    public void execute();

    public void pingService();

    public void terminateService();

}
