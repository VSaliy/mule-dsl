declare(
        flow("Hello")
                .then(log("#[payload]"))



);

onStart({

    def result = callFlow("Hello", "Hello");
    def payload = result.getMessage().getPayload()
    println "payload = $payload"
})
