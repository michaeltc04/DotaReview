package app.practice.carrmichaelt.com.dotareview;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;

        import com.squareup.picasso.Picasso;

        import java.util.ArrayList;
        import java.util.Map;

/**
 * Created by Wilson on 3/20/2015.
 */
public class MyRadiantAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> radiantHeroes;
    private Map<String, String[]> items, stats;

    public MyRadiantAdapter(Context context, ArrayList<String> radiantHeroes, Map<String, String[]> items, Map<String, String[]> stats) {
        this.context = context;
        this.radiantHeroes = radiantHeroes;
        this.items = items;
        this.stats = stats;
    }

    @Override
    public int getCount() { return radiantHeroes.size(); }

    @Override
    public Object getItem(int position) { return radiantHeroes.get(position); }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.dire_layout, null);
        } else {
            view = convertView;
        }

        view.setBackgroundColor(context.getResources().getColor(R.color.dire));

        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_dire_hero);
        Picasso.with(context)
                .load("http://cdn.dota2.com/apps/dota2/images/heroes/" + radiantHeroes.get(position) + "_full.png")
                .into(imageView);


        ImageView itemImageView1 = (ImageView) view.findViewById(R.id.image_view_dire_item_1);
        ImageView itemImageView2 = (ImageView) view.findViewById(R.id.image_view_dire_item_2);
        ImageView itemImageView3 = (ImageView) view.findViewById(R.id.image_view_dire_item_3);
        ImageView itemImageView4 = (ImageView) view.findViewById(R.id.image_view_dire_item_4);
        ImageView itemImageView5 = (ImageView) view.findViewById(R.id.image_view_dire_item_5);
        ImageView itemImageView6 = (ImageView) view.findViewById(R.id.image_view_dire_item_6);
        String[] heroItems = items.get("" + (position + 5));
        Picasso.with(context)
                .load("http://cdn.dota2.com/apps/dota2/images/items/" + items.get(Integer.parseInt(heroItems[0])) + "_lg.png")
                .into(itemImageView1);
        Picasso.with(context)
                .load("http://cdn.dota2.com/apps/dota2/images/items/" + items.get(Integer.parseInt(heroItems[1])) + "_lg.png")
                .into(itemImageView2);
        Picasso.with(context)
                .load("http://cdn.dota2.com/apps/dota2/images/items/" + items.get(Integer.parseInt(heroItems[2])) + "_lg.png")
                .into(itemImageView3);
        Picasso.with(context)
                .load("http://cdn.dota2.com/apps/dota2/images/items/" + items.get(Integer.parseInt(heroItems[3])) + "_lg.png")
                .into(itemImageView4);
        Picasso.with(context)
                .load("http://cdn.dota2.com/apps/dota2/images/items/" + items.get(Integer.parseInt(heroItems[4])) + "_lg.png")
                .into(itemImageView5);
        Picasso.with(context)
                .load("http://cdn.dota2.com/apps/dota2/images/items/" + items.get(Integer.parseInt(heroItems[5])) + "_lg.png")
                .into(itemImageView6);

        return view;
    }
}
