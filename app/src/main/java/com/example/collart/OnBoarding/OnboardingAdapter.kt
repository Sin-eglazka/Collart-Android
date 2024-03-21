import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.collart.OnBoarding.OnboardingItem
import com.example.collart.databinding.ItemOnboardingBinding

class OnboardingAdapter(private val items: List<OnboardingItem>) : PagerAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemOnboardingBinding.inflate(LayoutInflater.from(container.context), container, false)
        val item = items[position]
        binding.titleTextView.text = item.title
        binding.descriptionTextView.text = item.description
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}