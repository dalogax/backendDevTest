# Backend dev technical test SOLUTION

- This challenge was solved using a Layered architecture, retrieving the mock objects with a consumer.
- The consumer, takes care of the exceptions, to free ServiceImpl from any validation or exception control.
- The application uses an exception handler, _GlobalExceptionHandler_.
- Cache was used (ProductConsumer), to improve the performance

## Cache

### Tests before cache

![Before Cache](./assets/before_chache.png "Before Cache")

### Tests after cache

![After Cache](./assets/after_cache.png "After Cache")