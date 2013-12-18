package be.rvponp.build.util;

import be.rvponp.build.CommitViewerConfiguration;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;

/**
 * User: vermb
 * Date: 8/7/13
 * Time: 10:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class ADUserResolver {


    private static final String AD_URL = "ad.url";
    private static final String AD_USER = "ad.user";
    private static final String AD_PWD = "ad.pwd";
    private static final String AD_DOMAIN = "ad.domain";
    private static final CommitViewerConfiguration config = CommitViewerConfiguration.getInstance();
    private static final Logger log = Logger.getLogger(ADUserResolver.class);


    public static String getFullUsernameByID(String userid){
        String returnedValue = userid;
        Hashtable<String, String> env = new Hashtable<String, String>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, config.getProperty(AD_URL));
        env.put(Context.SECURITY_PRINCIPAL, config.getProperty(AD_USER)+'@'+ config.getProperty(AD_DOMAIN));
        env.put(Context.SECURITY_CREDENTIALS, config.getProperty(AD_PWD));
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
