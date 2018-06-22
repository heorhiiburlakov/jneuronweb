package web.storages;

import java.util.List;

public interface IStructMeta extends IStorageMeta{
    List<ILayerMeta> getLayers();
    List<IInputMeta> getInputs(String layerId);
    void saveResults(String layerId,List<IInputMeta> meta);
}
