package lt.cartogether.service;

import org.springframework.stereotype.Service;

@Service
public class TenantService {

    public Long getTenantId(String apiKey){

        if("demo-vilnius-key".equals(apiKey)){
            return 1L;
        }

        if("demo-kaunas-key".equals(apiKey)){
            return 2L;
        }

        throw new SecurityException("Invalid API key");
    }
}
