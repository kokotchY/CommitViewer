package be.rvponp.build.util;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

/**
 * User: vermb
 * Date: 8/7/13
 * Time: 10:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class ADUserResolver {

    private static final String SYSTEM_PROPERTY_NAME = "cv.config";
    private static final String AD_URL = "ad.url";
    private static final String AD_USER = "ad.user";
    private static final String AD_PWD = "ad.pwd";
    private static final String AD_DOMAIN = "ad.domain";

    public static String getFullUsernameByID(String userid){
        String returnedValue="";
        Hashtable<String, String> env = new Hashtable<String, String>();
        Properties configFile = new Properties();

        try {
            configFile.load(new FileInputStream(new File(System.getProperty(SYSTEM_PROPERTY_NAME))));
        } catch (IOException e) {
            System.err.println("Looking for "+SYSTEM_PROPERTY_NAME+"  property. File not found.");
            e.printStackTrace();
        }

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, configFile.getProperty(AD_URL));
        env.put(Context.SECURITY_PRINCIPAL, configFile.getProperty(AD_USER)+'@'+ configFile.getProperty(AD_DOMAIN));
        env.put(Context.SECURITY_CREDENTIALS, configFile.getProperty(AD_PWD));
        env.put(Context.REFERRAL, "follow");
        try {
            DirContext ctx = new InitialDirContext(env);

            Attributes matchAttrs = new BasicAttributes(true);
            matchAttrs.put(new BasicAttribute("mailNickname", userid));
            NamingEnumeration<SearchResult> answer = ctx.search("OU=Users,OU=Accounts,OU=RVP-ONP,DC=onprvp,DC=fgov,DC=be", matchAttrs);

            while (answer.hasMore()) {
                SearchResult sr = answer.next();
                returnedValue = sr.getName().replace("CN=","");
            }
            ctx.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnedValue;
    }

}
