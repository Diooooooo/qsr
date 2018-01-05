package com.qsr.sdk.util;

import com.qsr.sdk.lang.Parameter;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Env {

    private static String fileStorageUrl;
    private static String qrcodeUrl;
    private static String downloadUrl;
    private static String hostUrl;

    private static String sdkHostUrl;
    private static String platformHostUrl;

    private static Charset charset;
    private static int timeout = -1;
    private static int cacheProviderId = -1;

    private static int fileStorageProviderId = -1;

    private static String fileUploadDir;
    private static int fileUploadSize = -1;

    private static String fileCacheDir;

    private static Parameter parameter;

    private static String shell;
    //private static String shellScriptFile;

    private static String managementPassword;

    // private static String promotionFileWorkDir;

    private static long cacheTimeOut = -1;
    private static String deliveryManagerPhone;
    private static String aboutUs;

    private static String[] real_ip_headers = null;
    private static long ruleCheckDuration = -1;
    private static int serverId;
    private static List<String> filter_appstore_verify;

    private static List<String> ignore_promotion_keys;
    private static List<String> ignore_product_keys;
    private static List<String> ignore_device_imeis;
    private static String information;

    public static String[] getRealIpHeaders() {
        if (real_ip_headers == null) {
            real_ip_headers = parameter.as("real_ip_headers");
            if (real_ip_headers == null) {
                real_ip_headers = new String[0];
            }
        }
        return real_ip_headers;
    }

    public static List<String> getFilter_appstore_verify() {
        if (null == filter_appstore_verify) {
            filter_appstore_verify = new ArrayList<>();
            String[] menus = parameter.s("filter_appstore_verify", StringUtil.EMPTY_STRING).split(",");
            Arrays.stream(menus).forEach(target -> filter_appstore_verify.add(target));
        }
        return filter_appstore_verify;
    }

    public static String getManagementPassword() {
        if (managementPassword == null) {
            managementPassword = parameter.s("management_password",
                    "12345qwert");

        }
        return managementPassword;
    }

    public static String getShell() {
        if (shell == null) {
            shell = parameter.s("shell", "/usr/bin/bash");

        }
        return shell;
    }

    //	public static String getShellScriptFile() {
    //		if (shellScriptFile == null) {
    //			shellScriptFile = parameter.s("shellscript", "create.sh");
    //
    //		}
    //		return shellScriptFile;
    //	}

    public static String getHostUrl() {
        if (hostUrl == null) {
            hostUrl = parameter.s("host_url",
                    "http://xy.skywalker.19where.com/");
        }
        return hostUrl;
    }

    public static String getSdkHostUrl() {
        if (sdkHostUrl == null) {
            sdkHostUrl = parameter.s("sdk_host_url",
                    "http://sdk.skywalker.19where.com/");
        }
        return sdkHostUrl;
    }

    public static String getPlatformHostUrl() {
        if (platformHostUrl == null) {
            platformHostUrl = parameter.s("platform_host_url",
                    "http://xy.skywalker.19where.com/");
        }
        return platformHostUrl;
    }

    public static String getFileCacheDir() {
        if (fileCacheDir == null) {
            fileCacheDir = parameter.s("file_cache_dir", "/skywalker/appcache");
            File file = new File(fileCacheDir);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return fileCacheDir;
    }

    public static String getFileStorageUrl() {
        if (fileStorageUrl == null) {
            fileStorageUrl = parameter.s("filestorage_url",
                    "http://2s.19where.com/");
        }
        return fileStorageUrl;
    }

    public static String getQrcodeUrl() {
        if (qrcodeUrl == null) {
            qrcodeUrl = parameter.s("qrcode_url", "http://q.19w.me/");
        }
        return qrcodeUrl;
    }

    public static String getDownloadUrl() {

        if (downloadUrl == null) {
            downloadUrl = parameter.s("download_url", "http://19w.me/");
        }
        return downloadUrl;
    }

    public static Charset getCharset() {
        if (charset == null) {
            charset = Charset.forName(parameter.s("charset", "UTF-8"));
        }
        return charset;
    }

    public static int getTimeout() {
        if (timeout == -1) {
            timeout = parameter.i("timeout", 3000);
        }
        return timeout;
    }

    public static long getCacheTimeout() {
        if (cacheTimeOut == -1) {
            cacheTimeOut = parameter.i("cache_timeout", 600);
        }
        return cacheTimeOut;
    }

    public static int getCacheProviderId() {
        if (cacheProviderId == -1) {
            cacheProviderId = parameter.i("cache_provider_id", 2);
        }
        return cacheProviderId;
    }

    public static int getFileStorageProviderId() {
        if (fileStorageProviderId == -1) {
            fileStorageProviderId = parameter.i("file_storage_providerid", 1);
        }
        return fileStorageProviderId;
    }

    public static String getFileUploadDir() {
        if (fileUploadDir == null) {
            fileUploadDir = parameter.s("file_upload_dir", "/skywalker/upload");
            File file = new File(fileUploadDir);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return fileUploadDir;
    }

    public static int getFileUploadSize() {
        if (fileUploadSize == -1) {
            fileUploadSize = parameter.i("file_upload_size", 4000000);
        }
        return fileUploadSize;
    }

    private static void closeQuietly(Closeable c) {

        if (c != null) {
            try {
                c.close();
            } catch (Exception e) {
            }
        }
    }

    public static void load() {
        init();
    }

    public static void init() {

        InputStream is = null;
        try {
            is = WorkingResourceUtil.getInputStream("env.properties");
            Properties properties = new Properties();

            properties.load(is);

            parameter = new Parameter(properties);

//            serverId = Integer.parseInt(System.getenv("api_instance_id"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(is);
        }

    }

    public static int getServerId() {
        return serverId;
    }

    public static long getRuleCheckDuration() {
        if (ruleCheckDuration == -1) {
            ruleCheckDuration = parameter.i("rule_check_duration", 1000 * 60 * 3);
        }
        return ruleCheckDuration;
    }

    public static List<String> getIgnore_promotion_keys() {
        if (null == ignore_promotion_keys) {
            try (
                    InputStream is = WorkingResourceUtil.getInputStream("ignore.properties")
                    ){
                ignore_promotion_keys = new ArrayList<>();
                Properties properties = new Properties();
                properties.load(is);
                Parameter p = new Parameter(properties);
                ignore_promotion_keys.addAll(Arrays.asList(p.s("ignore_promotion_keys").split(",")));
            } catch (IOException t) {
                throw new RuntimeException(t);
            }
        }
        return ignore_promotion_keys;
    }

    public static List<String> getIgnore_product_keys() {
        if (null == ignore_product_keys) {
            try (
                    InputStream is = WorkingResourceUtil.getInputStream("ignore.properties")
                    ) {
                ignore_product_keys = new ArrayList<>();
                Properties properties = new Properties();
                properties.load(is);
                Parameter p = new Parameter(properties);
                ignore_product_keys.addAll(Arrays.asList(p.s("ignore_product_keys").split(",")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return ignore_product_keys;
    }

    public static List<String> getIgnore_device_imeis() {
        if (null == ignore_device_imeis) {
            try (
                    InputStream is = WorkingResourceUtil.getInputStream("ignore.properties")
                    ) {
                ignore_device_imeis = new ArrayList<>();
                Properties properties = new Properties();
                properties.load(is);
                Parameter p = new Parameter(properties);
                ignore_device_imeis.addAll(Arrays.asList(p.s("ignore_device_imeis").split(",")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return ignore_device_imeis;
    }

    public static String getDeliveryManagerPhone() {
        if (null == deliveryManagerPhone) {
            deliveryManagerPhone = parameter.s("delivery_manager_phone");
        }
        return deliveryManagerPhone;
    }

    public static String getAboutUs() {
        if (null == aboutUs) {
            aboutUs = parameter.s("about_us");
        }
        return aboutUs;
    }

    public static String getInformation() {
        if (null == information) {
            information = parameter.s("information");
        }
        return information;
    }
}
