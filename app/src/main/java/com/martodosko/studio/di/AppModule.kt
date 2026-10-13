// Inside AppModule.kt
import com.martodosko.studio.data.audio.AudioStateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAudioStateManager(): AudioStateManager {
        return AudioStateManager()
    }
}
