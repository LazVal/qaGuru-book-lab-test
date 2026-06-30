package api;

/**
 * Общий API-клиент — единая точка доступа к клиентам эндпоинтов.
 */
public class ApiClient {
    public final UsersApiClient users = new UsersApiClient();
    public final AuthApiClient auth = new AuthApiClient();
}
