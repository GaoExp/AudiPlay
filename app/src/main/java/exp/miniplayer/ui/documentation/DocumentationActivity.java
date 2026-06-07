package exp.miniplayer.ui.documentation;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import exp.miniplayer.R;

public class DocumentationActivity extends AppCompatActivity {

    private LinearLayout docList;
    private ScrollView docContent;
    private TextView docContentText;
    private TextView toolbarTitle;

    private static class DocItem {
        final int titleRes;
        final int subtitleRes;
        final String assetFile;

        DocItem(int titleRes, int subtitleRes, String assetFile) {
            this.titleRes = titleRes;
            this.subtitleRes = subtitleRes;
            this.assetFile = assetFile;
        }
    }

    private static final DocItem[] DOCS = {
        new DocItem(R.string.doc_readme, R.string.doc_readme_subtitle, "README.txt"),
        new DocItem(R.string.doc_panduan, R.string.doc_panduan_subtitle, "PANDUAN.txt"),
        new DocItem(R.string.doc_struktur, R.string.doc_struktur_subtitle, "STRUKTUR.txt"),

        new DocItem(R.string.doc_changelog, R.string.doc_changelog_subtitle, "CHANGELOG.txt"),
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documentation);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbarTitle = findViewById(R.id.toolbar_title);
        docList = findViewById(R.id.doc_list);
        docContent = findViewById(R.id.doc_content);
        docContentText = findViewById(R.id.doc_content_text);

        buildDocList();
    }

    private void buildDocList() {
        docList.removeAllViews();
        int padding = (int) (getResources().getDisplayMetrics().density * 12);

        for (int i = 0; i < DOCS.length; i++) {
            DocItem doc = DOCS[i];
            MaterialCardView card = new MaterialCardView(this);
            card.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            card.setCardElevation(0f);
            card.setStrokeWidth(1);
            card.setStrokeColor(getColor(R.color.outline));
            card.setClickable(true);
            card.setFocusable(true);
            card.setRadius(getResources().getDimensionPixelSize(R.dimen.corner_radius));

            LinearLayout content = new LinearLayout(this);
            content.setOrientation(LinearLayout.VERTICAL);
            content.setPadding(padding, padding, padding, padding);

            TextView titleView = new TextView(this);
            titleView.setText(doc.titleRes);
            titleView.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);

            TextView subtitleView = new TextView(this);
            subtitleView.setText(doc.subtitleRes);
            subtitleView.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodySmall);
            subtitleView.setTextColor(getColor(R.color.on_surface_variant));

            content.addView(titleView);
            content.addView(subtitleView);
            card.addView(content);

            final int index = i;
            card.setOnClickListener(v -> showDocument(index));

            docList.addView(card);

            if (i < DOCS.length - 1) {
                android.view.View spacer = new android.view.View(this);
                spacer.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) (getResources().getDisplayMetrics().density * 8)));
                docList.addView(spacer);
            }
        }
    }

    private void showDocument(int index) {
        DocItem doc = DOCS[index];
        toolbarTitle.setText(doc.titleRes);

        try {
            InputStream is = getAssets().open(doc.assetFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            docContentText.setText(sb.toString());
        } catch (IOException e) {
            docContentText.setText(R.string.doc_error);
            Toast.makeText(this, R.string.doc_error, Toast.LENGTH_SHORT).show();
        }

        docList.setVisibility(View.GONE);
        docContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (docContent.getVisibility() == View.VISIBLE) {
            docContent.setVisibility(View.GONE);
            docList.setVisibility(View.VISIBLE);
            toolbarTitle.setText(R.string.documentation);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
