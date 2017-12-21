package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class LeagueService extends Service {

    final static Logger logger = LoggerFactory.getLogger(LeagueService.class);

    public List<Map<String, Object>> getLeagues() throws ServiceException {
        return record2list(Db.find("", ""));
    }
}
