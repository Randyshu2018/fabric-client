package com.blockchain.gateway;

import com.blockchain.gateway.exception.MyException;
import com.blockchain.gateway.model.Result;
import com.blockchain.gateway.utils.FabricHelper;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author shurenwei
 */
public class HFClient {
    private static final Logger logger = LoggerFactory.getLogger(HFClient.class);

    public static Result invoke(User user, String... args){
        boolean flag = FabricHelper.submitTransaction(user,args);
        if(flag){
            return Result.getSuccInstance(null);
        }
        return Result.getErrorInstance(Result.FAIL);
    }

    public static Result query(User user,String...args){
        try {
            String result = FabricHelper.query(user,args);
            return Result.getSuccInstance(result);
        } catch (MyException e) {
            logger.error("HFCAUtils | userIsExist ", e.getMessage());
            return Result.getErrorInstance(e.getMessage());
        }
    }

    public static void main(String []args){
        JSONObject rr2 = new JSONObject();
        JSONObject selector = new JSONObject();
        selector.put("data.id","1");
        rr2.put("selector",selector);
//                {
//                    "selector":{
//                    "data.id":"1"
//                }
//                }
        try {
            System.out.println(query(com.blockchain.utils.FabricHelper.getMember("randy2006","Org1",null),rr2.toString(),"10000","").getData());
        } catch (com.blockchain.exception.MyException e) {
            e.printStackTrace();
        }
    }
}
