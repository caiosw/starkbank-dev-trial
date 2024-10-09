# Stark Bank Job Interview Dev Trial

This repository contains the code for the Stark Bank job interview dev trial.

# Requirements

- Java 11
- Gradle

# Description

For this challenge, I took the opportunity of learning a new solution, Azure Function (AZF).
This project is composed of 3 different AZFs that were deployed and are running on the Azure cloud:
## CreateInvoices:
  ![image](https://github.com/user-attachments/assets/5b2954a5-9d6e-454f-b58f-de1874e5f2cd)
  
  It's an AZF of type TimerTrigger, it receives a crontab like command (e.g. 0 "*/3 8-9 10 *").
  
  Since this feature don't have a parameter for end date and the challenge asked for it to run during only 24 hours, I set it to run during days 8/10 and 9/10 (first execution was on 2024-10-08 at 6am UTC), 
  with a function shouldRunCronJob to check if the end date and time was reached.

  It creates 8 to 12 Invoices every 3 hours with random amounts with the person selected from a list of 10 Person.

## StarkWebhook
  ![image](https://github.com/user-attachments/assets/6fabaca5-a4e6-4874-84d3-558956028d25)

  It's an AZF of type HttpTrigger, which the endpoint was registered to receive the webhooks from the Stark Bank. 
  
  It have the authLevel of FUNCTION, so it only accepts requests if the query parameter 'code' is sent. (https://dev-starkbank-devtrial-azf.azurewebsites.net/api/stark-webhook?code=<azure function key>)

  When a webhook is received, it uses the Parse.verify function from Stark Bank's sdk to validate that the sender is legit. If so, it sends the message to the Azure Service Bus queue. It also outputs the appropriate HTTP code.

## WebhookEventServiceBusConsumer
  ![image](https://github.com/user-attachments/assets/b2c51df8-a75c-4790-aa50-5383fa7d8543)

  Lastly, this AZF is of type ServiceBusQueueTrigger. It's triggered for every new message received in the Service Bus queue. 
  
  This implementation was made to ensure that messages are not lost, it also makes the process more reliable and resilient.
  
  The only implementation made was to read the Events of type Invoice. If they are paid, it sends a request for a Transfer creation, otherwise it'll just log a message and finish. 
  If any other type was received (should not happen, since the only webhook registered was the Invoice one) an exception will be raised.

  If the execution ends without an error, the AZF automatically mark the message as completed. If it finishes with an error, the message will return to the list to try again.

## Other informations
  - The mentioned Azure Functions are in the com.starkbank.devtrial.AzureFunction class;
  
  - To keep the critical data (private keys and connection strings) safe, those infos were stored in the Azure Key Vault, and added as App Settings in the AZF configuration, that way AZF updates the key automatically on deploy, but it can also be manually triggered in AZF panel. 
  That way we avoid looking frequently for secrets that'll not change often, reducing possible costs with constant requests on Azure Key Vault. 
  But, for local testing (env var IS_RUNNING_LOCALLY), I added the feature of getting the secret directly from the key vault, that way it's not needed for the dev to keep the secrets locally.

  - I made a lot of requests during development and testing. To make it easier to validade, in the "golden run" all Invoices and Transfers created to fulfill the challenge (Issues 8 to 12 Invoices every 3 hours to random people for 24 hours) were marked with the tag "devtrial". The previous creations were made with other tags, or without tags at all.

# Thoughs

  Azure Function looks like a simple way of creating endpoints, cronjobs, and consumers in a serverless way. It is also cheaper than other serverless alternatives, and could even be free depending on the quantity of executions per month, but it has its limitations, like:
  
  - Microsoft still didn't add some features present in the C# lib to Java's one, like the possibility for a ServiceBusQueueTrigger to return a message of type ServiceBusReceivedMessage. For the lack of this,
    I couldn't implement (commented code and more details in WebhookEventServiceBusConsumerFunction.run()) a logic to send malformed/invalid JSONs to the Dead Letter with a specific reason, that would avoid multiple reprocessing of a message that will surely fail again. That would be easily handled with other kinds of Service Bus consumers.

  - The logging have a delay of something like 5 minutes, something that can make the testing and tweaking a morous job.
