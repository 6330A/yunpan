package com.nenu.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileTypeList {
    private static Map<String, List<String>> map = new HashMap<>();
    static {
        map.put("Video", List.of("mp4", "avi", "flv", "mpeg", "mov", "mkv", "rmvb", "wmv"));
        map.put("Music", List.of("MP3", "aac", "wav", "flac", "ogg", "wma", "aiff", "m4a"));
        map.put("Photo", List.of("jpeg", "jpg", "png", "gif", "bmp", "tiff", "svg", "raw"));
        map.put("Document", List.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "csv", "html", "json", "md"));
    }

    public static List<String> get(String fileType) {
        if(fileType.equals("Else")) {
            return mergeAllLists();
        }else {
            return map.get(fileType);
        }
    }

    private static List<String> mergeAllLists() {
        List<String> allExtensions = new ArrayList<>();
        for (List<String> extensions : map.values()) {
            allExtensions.addAll(extensions);
        }
        return allExtensions;
    }
}
