/* ==========================================
 * Laverca Project
 * https://sourceforge.net/projects/laverca/
 * ==========================================
 * Copyright 2013 Laverca Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomicalab.mobileid;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for getting the sample configuration properties
 */
public class ExampleConf {

    private static final Log log = LogFactory.getLog(ExampleConf.class);
    
    public static final String CONF_LOCATION       = "conf/examples.conf";
    
    public static final String TRUSTSTORE_FILE     = "ssl.truststore.file";
    public static final String TRUSTSTORE_PASSWORD = "ssl.truststore.password";
    public static final String KEYSTORE_FILE       = "ssl.keystore.file";
    public static final String KEYSTORE_PASSWORD   = "ssl.keystore.password";
    public static final String KEYSTORE_TYPE       = "ssl.keystore.type";
    
    public static final String MSSP_ID			   = "mssp.id";
    
    public static final String AP_ID               = "ap.id";
    public static final String AP_PASSWORD         = "ap.password";
    public static final String SIGNATURE_URL       = "mssp.signature.url";
    public static final String STATUS_URL          = "mssp.status.url";
    public static final String RECEIPT_URL         = "mssp.receipt.url";
    public static final String PROFILE_URL		   = "mssp.profile.url";
    public static final String REGISTERATION_URL   = "mssp.registration.url";
    
    public static Properties getProperties(String confPath){
        File f = new File(confPath);
        Properties properties = new Properties();
        FileInputStream is;
        try {
            is = new FileInputStream(f);
            properties.load(is);
            is.close();
        } catch (Exception e) {
            log.fatal("Could not load properties");
        }
        return properties;
    }

    public static Properties getProperties(){
        return getProperties(CONF_LOCATION);
    }
    
}
