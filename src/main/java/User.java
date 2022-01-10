import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"username"})
public class User {
    private final String username;
    private final String password;
    private long lastUpdatedTime = System.currentTimeMillis();
    private String ip;
    private String port;
}
