package conventional.commits

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SemVerTest {
    @ParameterizedTest
    @CsvSource(value = [
        // tag,     major, minor, patch
        "v0.0.0 ,   0    , 0    , 0",
        "v1.0.0 ,   1    , 0    , 0",
        "v1.10.0,   1    , 10   , 0",
        "v1.1.10,   1    , 1    , 10",
        "v21.1.10, 21    , 1    , 10",
    ])
    fun `should create from tag version and map back again`(ver: String, major: Int, minor: Int, patch: Int) {
        // expect
        assertThat(SemVer.from(ver)).isEqualTo(SemVer(major, minor, patch))
        assertThat(SemVer(major, minor, patch).toTag()).isEqualTo(ver)
    }

    @ParameterizedTest
    @CsvSource(value = [
        // before,  increment   , after
        "v0.0.0   , MAJOR       , v1.0.0  ",
        "v1.0.0   , MINOR       , v1.1.0  ",
        "v1.10.0  , PATCH       , v1.10.1 ",
        "v9.1.10  , MAJOR       , v10.1.10 ",
        "v21.1.10 , MINOR       , v21.2.10",
        "v19.1.10 , PATCH       , v19.1.11",
        "v19.1.10 , NONE        , v19.1.10",
    ])
    fun `should increment version depended on incrementer`(
            versionBefore: String, increment: VersionIncrement, versionAfter: String
    ) {
        // expect
        assertThat(SemVer.from(versionBefore).increment(increment).toTag()).isEqualTo(versionAfter)
    }

}