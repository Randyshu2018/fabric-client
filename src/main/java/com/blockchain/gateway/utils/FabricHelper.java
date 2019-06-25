package com.blockchain.gateway.utils;

import com.blockchain.gateway.exception.MyException;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.gateway.impl.WalletIdentity;
import org.hyperledger.fabric.sdk.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import java.nio.file.*;

import java.util.concurrent.TimeoutException;

/**
 * @author shurenwei
 */
public class FabricHelper {
    private static final Logger logger = LoggerFactory.getLogger(FabricHelper.class);

    public static String query(User user ,String... args)throws MyException{
        try {
            byte[] result = getContract(getNetwork(user)).evaluateTransaction("queryWithPagination",args);
            return new String(result);
        } catch (GatewayException e) {
            e.printStackTrace();
            throw new MyException("query error");
        }
    }

    public static boolean submitTransaction(User user ,String... args){
        try {
            getContract(getNetwork(user)).submitTransaction("save",args);
            return true;
        } catch (GatewayException e) {
            e.printStackTrace();
            return false;
        } catch (TimeoutException e) {
            e.printStackTrace();
            return false;
        } catch (MyException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Contract getContract(User user ,String channelName ,String name)throws MyException{
        return getNetwork(user,channelName).getContract(name);
    }

    public static Contract getContract(Network network)throws MyException{
        return network.getContract("supervision");
    }

    public static Network getNetwork(User user,String channelName)throws MyException{
        try {
            return getGateway(user).getNetwork(channelName);
        } catch (GatewayException e) {
            e.printStackTrace();
            throw new MyException("getNetwork 获取失败");
        } catch (MyException e) {
            e.printStackTrace();
            throw new MyException("getNetwork 获取失败");

        }
    }
    public static Network getNetwork(User user)throws MyException{
        try {
            return getGateway(user).getNetwork("mychannel");
        } catch (GatewayException e) {
            e.printStackTrace();
            throw new MyException("getNetwork 获取失败"+e.getMessage());
        } catch (MyException e) {
            e.printStackTrace();
            throw new MyException("getNetwork 获取失败+"+e.getMessage());

        }
    }


    public static Gateway getGateway(User user) throws MyException {
        try {
            Wallet wallet = Wallet.createFileSystemWallet(Paths.get("/opt/wallet"));
            if(!wallet.exists(user.getName())){
                System.out.println("enrollment:"+user.getEnrollment());
                if(user.getEnrollment() == null ){
                    throw new MyException("User enrollment can not be null");
                }
                wallet.put(user.getName(),new WalletIdentity(user.getMspId(),user.getEnrollment().getCert(),user.getEnrollment().getKey()));
            }
            InputStream input = Gateway.class.getResourceAsStream("/connection.yaml");
            Gateway.Builder builder = Gateway.createBuilder();
            builder.identity(wallet,user.getName()).networkConfig(input);
            // create a gateway connection
            try (Gateway gateway = builder.connect()) {
                // get the network and contract
                return gateway;
            } catch (GatewayException e1) {
                e1.printStackTrace();
                throw new MyException("gateway 获取失败,Gateway异常"+ e1.getMessage());
            }

        } catch (GatewayException e) {
            e.printStackTrace();
            throw new MyException("gateway 获取失败,Gateway异常:"+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new MyException("gateway 获取失败,IO异常:"+e.getMessage());
        }
    }
}
