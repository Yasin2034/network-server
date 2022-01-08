import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"username","password"})
public class User {
    private final String username;
    private final String password;
    private String ip;
    private String port;
}
