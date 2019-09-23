(function ($) {

$(document).ready(function(){
   $("#qvantel").on("click", function () {
       $("#maincont").load("assets/continents/welcome.html");
   });
   $("#AllContinents").on("click", function () {
       $("#maincont").load("assets/continents/continent_list.html");
   });
   $("#addContinent").on("click", function () {
        $("#maincont").load("assets/continents/add_continent.html");
   });
   $("#continentOfACountry").on("click", function () {
        $("#maincont").load("assets/continents/continent_of_country.html");
   });
   $("#citiesOfAContinent").on("click", function () {
        $("#maincont").load("assets/continents/cities_of_continent.html");
   });
   $("#countriesInContinent").on("click", function () {
        $("#maincont").load("assets/continents/countries_in_continent.html");
   });
   $("#addCitiesToCountry").on("click", function () {
        $("#maincont").load("assets/continents/add_cities.html");
   });
   $("#groupCities").on("click", function () {
         $("#maincont").load("assets/continents/group_cities.html");
   });

   $.fn.serializeFormJSON = function () {

           var o = {};
           var a = this.serializeArray();
           $.each(a, function () {
               if (o[this.name]) {
                   if (!o[this.name].push) {
                       o[this.name] = [o[this.name]];
                   }
                   o[this.name].push(this.value || '');
               } else {
                   o[this.name] = this.value || '';
               }
           });
           return o;
       };

});

})(jQuery);


