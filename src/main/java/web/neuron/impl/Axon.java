package web.neuron.impl;

import web.neuron.IAxon;
import web.neuron.INConnection;
import web.neuron.IWeight;
import web.signals.ISignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Axon implements IAxon {
    private HashMap<Class<? extends ISignal>, List<INConnection>> connectionMap;
    private HashMap<Integer, HashMap<Long,List<INConnection>>> onDestroyMap;

    public Axon() {
        this.connectionMap = new HashMap<>();
        this.onDestroyMap=new HashMap<>();
    }

    @Override
    public <S extends ISignal> void putConnection(Class<S> cl, INConnection<S> connection) {
        if(connectionMap.containsKey(cl)){
            connectionMap.get(cl).add(connection);
        }else{
            List<INConnection> tlist= new ArrayList<>();
            tlist.add(connection);
            connectionMap.put(cl,tlist);
        }
        if(onDestroyMap.containsKey(connection.getTargetLayerId())){
            HashMap<Long,List<INConnection>> tMap= onDestroyMap.get(connection.getTargetLayerId());
            if(tMap.containsKey(connection.getTargetNeuronId())){
                tMap.get(connection.getTargetNeuronId()).add(connection);
            }else{
                List<INConnection> tlist= new ArrayList<>();
                tlist.add(connection);
                tMap.put(connection.getTargetNeuronId(),tlist);
            }
        }else{
            HashMap<Long,List<INConnection>> tMap =new HashMap<>();
            List<INConnection> tlist= new ArrayList<>();
            tlist.add(connection);
            tMap.put(connection.getTargetNeuronId(),tlist);
            onDestroyMap.put(connection.getTargetLayerId(),tMap);
        }
    }



    @Override
    public void cleanConnections() {
        connectionMap.clear();


    }

    @Override
    public HashMap<ISignal, List<INConnection>> processSignal(List<ISignal> signal) {
        HashMap<ISignal,List<INConnection>> result=new HashMap<>();
        for(ISignal s:signal){
            Class<? extends ISignal>  cl= s.getCurrentClass();
            if(connectionMap.containsKey(cl)){
                for(INConnection con:connectionMap.get(cl)){
                    ISignal resSignal= con.getWeight().process(s);
                    if(result.containsKey(resSignal)){
                        result.get(resSignal).add(con);
                    }else{
                        List<INConnection> cons= new ArrayList<>();
                        cons.add(con);
                        result.put(resSignal,cons);
                    }
                }
            }else {
                //TODO:add warn for logger;

            }
        }
        return result;
    }



    @Override
    public String toJSON() {
        return null;
    }

    @Override
    public void destroyConnection(int layerId, Long neuronId, Class<? extends ISignal> clazz) {
        if(onDestroyMap.containsKey(layerId)&&onDestroyMap.get(layerId).containsKey(neuronId)){
        List<INConnection> conns=onDestroyMap.get(layerId).get(neuronId);
        for(INConnection c:conns){
            if(c.getWeight().getSignalClass().equals(clazz)){
                connectionMap.get(clazz).remove(c);
                conns.remove(c);
                break;
            }
        }
        }
    }

    @Override
    public void changeWeight(int layerId, Long neuronId, ISignal signal) {
        if(onDestroyMap.containsKey(layerId)&&onDestroyMap.get(layerId).containsKey(neuronId)){
            List<INConnection> conns=onDestroyMap.get(layerId).get(neuronId);
            for(INConnection c:conns){
                c.getWeight().changeWeight(signal);
            }
        }
    }

    @Override
    public void changeWeight(int layerId, Long neuronId, Class<? extends ISignal> clazz, ISignal signal) {
        if(onDestroyMap.containsKey(layerId)&&onDestroyMap.get(layerId).containsKey(neuronId)){
            List<INConnection> conns=onDestroyMap.get(layerId).get(neuronId);
            for(INConnection c:conns){
                if(c.getWeight().getSignalClass().equals(clazz)){
                    c.getWeight().changeWeight(signal);
                }
            }
        }
    }

    @Override
    public void changeWeight(ISignal signal) {
        for(Class<? extends ISignal> cl:connectionMap.keySet()){
            for(INConnection con: connectionMap.get(cl)){
                con.getWeight().changeWeight(signal);
            }
        }

    }


}
