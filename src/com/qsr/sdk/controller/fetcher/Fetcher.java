package com.qsr.sdk.controller.fetcher;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import com.qsr.sdk.lang.Parameter;

import java.util.*;

public abstract class Fetcher extends Parameter {

    private final Controller controller;
    private final Map<String, Object> parameters = new LinkedHashMap<>();
    //protected List<UploadFile> uploadFiles=new ArrayList<>();
    boolean fetched = false;

    public Fetcher(Controller controller) {
        super();
        super.setParameterMap(parameters);
        this.controller = controller;
    }

//	public final void setContext(Controller controller) {
//		this.controller = controller;
////		this.setParameterMap(getParameters());
//	}

    public Controller getController() {
        return controller;
    }

    public Fetcher fetch() {
        if (!fetched) {
            parameters.putAll(buildParameterMap());
            fetched = true;
        }
        return this;
    }

    public Map<String, Object> getParameters() {
        if (!fetched) {
            fetch();
        }
        return parameters;
    }

    public List<UploadFile> getUploadFiles() {
        List<UploadFile> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : getParameters().entrySet()) {
            if (entry.getValue() instanceof UploadFile) {
                list.add((UploadFile) entry.getValue());
            }
        }
        return Collections.unmodifiableList(list);
    }

    protected abstract Map<String, Object> buildParameterMap();

}
