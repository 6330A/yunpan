package com.nenu.service.impl;

import com.nenu.mapper.FileSetMapper;
import com.nenu.pojo.FileSet;
import com.nenu.service.FileSetService;
import com.nenu.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FileSetServiceImpl implements FileSetService {
    @Autowired
    private FileSetMapper fileSetMapper;

    @Override
    public List<FileSet> getZeroReferenced() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = Integer.valueOf(claims.get("userId").toString());

        return fileSetMapper.getZeroReferenced(userId);
    }

    @Override
    public void deleteZeroReferenced() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = Integer.valueOf(claims.get("userId").toString());

        fileSetMapper.deleteZeroReferenced(userId);
    }
}
