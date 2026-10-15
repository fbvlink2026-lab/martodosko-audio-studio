// Inside AppModule.kt
import android.content.Context
import com.martodosko.studio.data.audio.AudioStateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAudioStateManager(@ApplicationContext context: Context): AudioStateManager {
        return AudioStateManager(context)
    }
}
