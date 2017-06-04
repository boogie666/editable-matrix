(ns editable-matrix.core
    (:require [reagent.core :as reagent :refer [atom]]))


(defn cell [val]
  {:val val :edit false})

(def table-data
  (atom {:data {\a {1 (cell "AA") 2 (cell "AB") 3 (cell "AC") 4 (cell "AD")}
                \b {1 (cell "BA") 2 (cell "BB") 3 (cell "BC") 4 (cell "BD")}
                \c {1 (cell "CA") 2 (cell "CB") 3 (cell "CC") 4 (cell "CD")}}
         :layout {:cols [\a \b \c]
                  :rows [1 2 3]}}))


(defn sort-table [what how]
  (let [data (get @table-data what)]
    (if (= how :asc)
      (swap! table-data update-in [:layout what] #(sort (fn [a b] (compare a b)) %))
      (swap! table-data update-in [:layout what] #(sort (fn [a b] (compare b a)) %)))))

(defn cell-edit-toggle [col row]
  (swap! table-data update-in [:data col row :edit] not))

(defn cell-update-val [col row val]
  (swap! table-data assoc-in [:data col row :val] val))

(defn next-col [col]
  (.fromCharCode js/String (+ (.charCodeAt col 0) 1)))

(defn last-item [xs]
  (reduce max xs))

(defn add-thing! [what how]
  (let [last-col (-> @table-data
                    (get-in [:layout what])
                    last-item
                    how)]
    (swap! table-data update-in [:layout what] conj last-col)))


(defn home-page []
  (let [{:keys [data layout]} @table-data
        layout {:cols (sort-by :order (:cols layout))
                :rows (sort-by :order (:rows layout))}]
    [:div
      [:button {:on-click #(sort-table :rows :desc)} "Rows Up"]
      [:button {:on-click #(sort-table :rows :asc)} "Rows Down"]
      [:button {:on-click #(sort-table :cols :desc)} "Cols Left"]
      [:button {:on-click #(sort-table :cols :asc)} "Cols Right"]
      [:table
        [:tr [:td]
          (for [col (:cols layout)]
            [:th col])
          [:th [:button {:on-click #(add-thing! :cols next-col)} "+"]]]
        (for [row (:rows layout)]
          [:tr
            [:td row]
            (for [col (:cols layout)]
              (let [cell (get-in data [col row])]
                [:td {:on-double-click #(cell-edit-toggle col row)}
                    (if (:edit cell)
                      [:input {:value (:val cell)
                               :on-blur #(cell-edit-toggle col row)
                               :on-change #(cell-update-val col row (-> % .-target .-value))}]
                      [:span (:val cell)])]))])
        [:tr [:td [:button {:on-click #(add-thing! :rows inc)} "+"]]]]]))



;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
