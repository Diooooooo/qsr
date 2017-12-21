package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class DataService extends Service {

    final static Logger logger = LoggerFactory.getLogger(DataService.class);

    public List<Map<String, Object>> getDataList() throws ServiceException {
        return record2list(Db.find(""));
    }

    public Map<String, Object> getDataInfo() throws ServiceException {
        return record2map(Db.findFirst(""));
    }

}
