package sunnyweather.rokuan.com.sunny.data;

/**
 * Created by LEBEAU Christophe on 31/05/15.
 */
public class City {
    private Long id;
    private String name;
    private String country;

    public City(long cityId, String cityName, String cityCountry){
        id = cityId;
        name = cityName;
        country = cityCountry;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }

        return (o instanceof City) && (((City)o).id == this.id);
    }

    @Override
    public String toString(){
        return this.name + "," + this.country;
    }
}
