package ums;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lanswon.generator.rich.MetaData;
import com.lanswon.util.JWTUtil;

import io.jsonwebtoken.Claims;

public class TestJwt {

    public static void main(String[] args) {
        String username = "zs";
        String password = "1234";

        MetaData metaData = new MetaData();
        metaData.initPut("username", username);
        metaData.initPut("password", password);
        metaData.initPut("url", "http://192.168.44.49:8080/ums/");

        ObjectMapper om = new ObjectMapper();

        String token = "";
        try {
            token = JWTUtil.createJWT(username, om.writeValueAsString(metaData), 1000 * 60 * 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("token:" + token);

        Claims claims = null;
        token = "ZXlKaGJHY2lPaUpJVXpJMU5pSjkuZXlKcWRHa2lPaUl3TURFaUxDSnBZWFFpT2pFMU1UYzVNREkwTXpZc0luTjFZaUk2SW50Y0luSmxaM1JwYldWY0lqcGNJakl3TVRndE1ERXRNekJjSWl4Y0luSnZiR1ZjSWpwY0lqQXdNVndpTEZ3aWMyVjRYQ0k2WENJeFhDSXNYQ0p0YjJKcGJHVmNJanBjSWpFMk9EVTBOVEkyTkRVeVhDSXNYQ0prWlhCMFhDSTZNU3hjSW5WelpYSnBaRndpT2x3aU1EQXhYQ0lzWENKMWNteGNJanBjSW1oMGRIQTZMeTh4T1RJdU1UWTRMalEwTGpRNU9qZ3dPREF2ZFcxekwxd2lMRndpY0dGemMzZHZjbVJjSWpwY0lqZ3haR001WW1SaU5USmtNRFJrWXpJd01ETTJaR0prT0RNeE0yVmtNRFUxWENJc1hDSnRaWFJoVG1GdFpWd2lPbHdpVkVGQ1gxVlRSVkpjSWl4Y0luVnpaWEpuY205MWNGd2lPbHdpTURBeFhDSXNYQ0pwWkZ3aU9sd2lNVndpTEZ3aVpXMWhhV3hjSWpwY0lqRXlNelExTmtCc2N5NWpiMjFjSWl4Y0luVnpaWEp1WVcxbFhDSTZYQ0xsaFlQb2lyTmNJaXhjSW5OMFlYUjFjMXdpT2x3aU1Wd2lmU0lzSW1WNGNDSTZNekF6TlRnd05EZzNNbjAuV1R2OGF1UkgwSFhrN2t2SGZNTXhFX1dJY09GMmV4UlhjRmF3NHFZR0pXQQ%3D%3D";
        try {
            claims = JWTUtil.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String jmd = claims.getSubject();
        System.out.println("jmd:" + jmd);
        try {
            MetaData md = om.readValue(jmd, MetaData.class);
            System.out.println("MetaData : " + md);
            String url = md.getString("url");
            System.out.println("url:" + url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
