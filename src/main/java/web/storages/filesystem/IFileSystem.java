package web.storages.filesystem;

import java.util.List;

public interface IFileSystem<S extends IFileSystemItem> {
    S getItem(String path);
    String read(S path);
    boolean writeCreate(String content,S path);
    boolean writeUpdate(String content,S path, int startFrom);
    boolean delete(String path);
    boolean delete(S path);
    boolean deleteContent(S path, int startFrom, int amount);
    List<S> listFiles( S file );
    String getFolderSeparator();
}
