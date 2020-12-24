/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ao.vpos.vpos;

import ao.vpos.vpos.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class VposBuilder {
    public static HttpResponse<String> newPayment(String mobile, String amount, String token) throws MalformedURLException, IOException, InterruptedException {        
        var client = HttpClient.newHttpClient();
        
        var body = new HashMap<String, String>();
    
        body.put("type", "payment");
        body.put("pos_id", System.getenv("GPO_POS_ID"));
        body.put("callback_url", System.getenv("VPOS_PAYMENT_CALLBACK_URL"));
        body.put("mobile", mobile);
        body.put("amount", amount);
        
        var objectMapper = new ObjectMapper();
        var requestBody = objectMapper.writeValueAsString(body);
        
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(new URL("https://sandbox.vpos.ao") + "/api/v1/transactions"))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static Transaction getTransaction(String transactionId, String token) throws MalformedURLException, IOException, InterruptedException {
        var client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
          .GET()
          .header("Accept", "application/json")
          .header("Content-Type", "application/json")
          .header("Authorization", "Bearer " + token)
          .uri(URI.create(new URL("https://sandbox.vpos.ao") + String.format("/api/v1/transactions/%s", transactionId)))
          .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(), Transaction.class);
    }
}
