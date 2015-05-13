package nz.co.scuff.android.data;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Callum on 14/05/2015.
 */
public abstract class BaseModel extends Model {

    protected <T extends Model> List<T> getManyThrough(Class<T> targetClass, Class joinClass, String targetForeignKeyInJoin, String foreignKeyInJoin){
        return new Select()
                .from(targetClass)
                .as("target_model")
                .join(joinClass)
                .as("join_model")
                .on("join_model." + targetForeignKeyInJoin + " = " + "target_model.id")
                .where("join_model." + foreignKeyInJoin + " = ?", this.getId())
                .execute();
    }

}
