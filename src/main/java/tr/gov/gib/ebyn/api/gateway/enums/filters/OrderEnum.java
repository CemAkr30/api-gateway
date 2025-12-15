package tr.gov.gib.ebyn.api.gateway.enums.filters;

public enum OrderEnum
{
    /**
     *  Buradaki sıralama önemlidir.
     *  Eğer ki yeni bir order eklenirse trace'den sonra gelmesi
     *  gerekiyorsa trace_filter'dan sonra eklenmelidir.
     *  İhtiyaca göre sıralama değiştirilebilir.
     */
    PRE_LOGGING_FILTER,
    TRACE_FILTER,
    STRIP_BASE_PATH_FILTER,
    AUTHORIZATION_FILTER,
    POST_LOGGING_FILTER;
}
